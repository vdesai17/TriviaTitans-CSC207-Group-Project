package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.*;
import trivia.interface_adapter.presenter.LoadQuizViewModel;
import trivia.use_case.load_quiz.LoadQuizOutputData;
import trivia.use_case.start_quiz.StartQuizOutputData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * LoadQuizScreen — allows players to view and open their saved custom quizzes.
 * 
 * ✅ FULLY REFACTORED TO FOLLOW CLEAN ARCHITECTURE:
 * - Uses LoadQuizController (not DAO)
 * - Uses StartQuizController to load quiz questions
 * - Observes LoadQuizViewModel
 * - No direct DAO access
 */
public class LoadQuizScreen extends JPanel implements PropertyChangeListener {
    private final JFrame frame;
    private final Player player;
    
    // Controllers
    private final LoadQuizController loadQuizController;
    private final StartQuizController startQuizController;
    private final CompleteQuizController completeQuizController;
    private final GenerateFromWrongController generateFromWrongController;
    
    // ViewModel
    private final LoadQuizViewModel viewModel;
    
    // UI Components
    private final DefaultListModel<String> listModel;
    private final JList<String> quizList;
    private List<LoadQuizOutputData.QuizSummary> currentQuizzes;

    public LoadQuizScreen(JFrame frame,
                          Player player,
                          LoadQuizController loadQuizController,
                          StartQuizController startQuizController,
                          LoadQuizViewModel viewModel,
                          CompleteQuizController completeQuizController,
                          GenerateFromWrongController generateFromWrongController) {
        this.frame = frame;
        this.player = player;
        this.loadQuizController = loadQuizController;
        this.startQuizController = startQuizController;
        this.viewModel = viewModel;
        this.completeQuizController = completeQuizController;
        this.generateFromWrongController = generateFromWrongController;

        // ✅ Listen to ViewModel
        viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout(20, 20));
        ThemeUtils.applyGradientBackground(this);

        // --- Header ---
        JLabel title = new JLabel("Your Saved Quizzes", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        add(title, BorderLayout.NORTH);

        // --- Center Panel (Glass Effect) ---
        JPanel centerPanel = ThemeUtils.createGlassPanel(60);
        centerPanel.setLayout(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        listModel = new DefaultListModel<>();
        listModel.addElement("Loading quizzes...");

        quizList = new JList<>(listModel);
        quizList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quizList.setFont(ThemeUtils.BODY_FONT);
        quizList.setBackground(new Color(255, 255, 255, 230));
        quizList.setSelectionBackground(new Color(0, 150, 150, 100));
        quizList.setSelectionForeground(Color.BLACK);
        quizList.setFixedCellHeight(35);
        quizList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(quizList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Select a Quiz"));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Buttons ---
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 150, 50, 150));

        JButton backButton = createStyledButton(
                "Back to Home",
                new Color(200, 70, 70),
                new Color(220, 90, 90),
                e -> goBackHome());

        JButton openButton = createStyledButton(
                "Open Selected Quiz",
                ThemeUtils.MINT,
                ThemeUtils.MINT_HOVER,
                e -> openSelectedQuiz());

        bottomPanel.add(backButton);
        bottomPanel.add(openButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // ✅ Load quizzes using controller
        loadQuizController.loadQuizzes(player.getPlayerName());
    }

    private JButton createStyledButton(String text, Color base, Color hover,
                                       java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(ThemeUtils.BUTTON_FONT);
        button.setBackground(base);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(hover); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(base); }
        });
        button.addActionListener(listener);
        return button;
    }

    /**
     * ✅ CLEAN ARCHITECTURE: React to ViewModel changes
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(LoadQuizViewModel.QUIZZES_PROPERTY)) {
            updateQuizList();
        } else if (evt.getPropertyName().equals(LoadQuizViewModel.ERROR_PROPERTY)) {
            showError();
        }
    }

    /**
     * Update UI with quizzes from ViewModel
     */
    private void updateQuizList() {
        currentQuizzes = viewModel.getQuizzes();
        listModel.clear();

        if (currentQuizzes == null || currentQuizzes.isEmpty()) {
            listModel.addElement("No saved quizzes found.");
        } else {
            for (LoadQuizOutputData.QuizSummary quiz : currentQuizzes) {
                listModel.addElement(quiz.getTitle() + "  (" + quiz.getCategory() + ")");
            }
        }
    }

    /**
     * Show error from ViewModel
     */
    private void showError() {
        String errorMessage = viewModel.getErrorMessage();
        listModel.clear();
        listModel.addElement("Error: " + errorMessage);
    }

    /**
     * ✅ CLEAN ARCHITECTURE: Use StartQuizController to load quiz
     */
    private void openSelectedQuiz() {
        int index = quizList.getSelectedIndex();
        
        if (currentQuizzes == null || currentQuizzes.isEmpty() || index == -1) {
            JOptionPane.showMessageDialog(frame, 
                "Please select a valid quiz.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LoadQuizOutputData.QuizSummary selected = currentQuizzes.get(index);
            
            // ✅ Use controller to start quiz
            StartQuizOutputData quizData = startQuizController.startQuiz(
                selected.getQuizId(), 
                player.getPlayerName()
            );

            // Clean up listener
            viewModel.removePropertyChangeListener(this);

            // Navigate to quiz screen
            frame.getContentPane().removeAll();
            frame.add(new QuizScreen(frame, quizData.getQuestions(), player, 
                    completeQuizController, generateFromWrongController));
            frame.revalidate();
            frame.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, 
                "Failed to load quiz: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBackHome() {
        // Clean up listener
        viewModel.removePropertyChangeListener(this);
        
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(frame, player, generateFromWrongController,
                completeQuizController, null));
        frame.revalidate();
        frame.repaint();
    }
}