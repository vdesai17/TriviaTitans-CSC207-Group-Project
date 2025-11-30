package trivia.framework.ui;

import trivia.entity.Player;
import trivia.entity.Quiz;
import trivia.entity.Question;
import trivia.interface_adapter.controller.CompleteQuizController;
import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.interface_adapter.controller.LoadQuizController;
import trivia.interface_adapter.presenter.LoadQuizViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * LoadQuizScreen — allows players to view and open their saved custom quizzes.
 * Now follows Clean Architecture:
 *  - UI depends only on the Controller and ViewModel
 *  - No direct DAO access
 */
public class LoadQuizScreen extends JPanel {
    private final JFrame frame;
    private final Player player;
    private final LoadQuizController loadQuizController;
    private final LoadQuizViewModel loadQuizViewModel;
    private final CompleteQuizController completeQuizController;
    private final GenerateFromWrongController generateFromWrongController;

    public LoadQuizScreen(JFrame frame,
                          Player player,
                          LoadQuizController loadQuizController,
                          LoadQuizViewModel loadQuizViewModel,
                          CompleteQuizController completeQuizController,
                          GenerateFromWrongController generateFromWrongController) {
        this.frame = frame;
        this.player = player;
        this.loadQuizController = loadQuizController;
        this.loadQuizViewModel = loadQuizViewModel;
        this.completeQuizController = completeQuizController;
        this.generateFromWrongController = generateFromWrongController;

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

        // Retrieve quizzes through controller instead of DAO
        List<Quiz> playerQuizzes = loadQuizController.loadQuizzesForPlayer(player.getPlayerName());
        loadQuizViewModel.setQuizzes(playerQuizzes);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (playerQuizzes == null || playerQuizzes.isEmpty()) {
            listModel.addElement("No saved quizzes found.");
        } else {
            for (Quiz q : playerQuizzes) {
                listModel.addElement(q.getTitle() + "  (" + q.getCategory() + ")");
            }
        }

        JList<String> quizList = new JList<>(listModel);
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
                this::goBackHome);

        JButton openButton = createStyledButton(
                "Open Selected Quiz",
                ThemeUtils.MINT,
                ThemeUtils.MINT_HOVER,
                e -> openSelectedQuiz(quizList, playerQuizzes));

        bottomPanel.add(backButton);
        bottomPanel.add(openButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /** Creates unified styled button with hover transitions. */
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

    /** Opens the selected quiz from the list */
    private void openSelectedQuiz(JList<String> quizList, List<Quiz> playerQuizzes) {
        int index = quizList.getSelectedIndex();
        if (playerQuizzes == null || playerQuizzes.isEmpty() || index == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a valid quiz.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Quiz selected = playerQuizzes.get(index);
        List<Question> questions = selected.getQuestions();
        if (questions == null || questions.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "This quiz has no questions.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        frame.getContentPane().removeAll();
        frame.add(new QuizScreen(frame, questions, player, completeQuizController, generateFromWrongController));
        frame.revalidate();
        frame.repaint();
    }

    /** Returns to Home Screen */
    private void goBackHome(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(
                frame,
                player,
                generateFromWrongController,
                completeQuizController,
                // We don’t need quizDAO anymore since controller handles that
                null
        ));
        frame.revalidate();
        frame.repaint();
    }
}
