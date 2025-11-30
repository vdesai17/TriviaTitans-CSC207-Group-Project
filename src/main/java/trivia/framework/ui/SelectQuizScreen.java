package trivia.framework.ui;

import trivia.entity.Player;
import trivia.entity.Question;
import trivia.interface_adapter.api.APIManager;
import trivia.interface_adapter.controller.CompleteQuizController;
import trivia.interface_adapter.controller.SelectQuizController;
import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.use_case.select_quiz.SelectQuizInteractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * SelectQuizScreen — choose quiz category, difficulty, or practice from wrong questions.
 * 
 * ✅ FULLY REFACTORED TO FOLLOW CLEAN ARCHITECTURE:
 * - NO direct DAO access
 * - Uses SelectQuizController for API quizzes
 * - Uses GenerateFromWrongController for practice quizzes
 * - No business logic in UI
 */
public class SelectQuizScreen extends JPanel {
    private final JFrame frame;
    private final Player currentPlayer;
    private final SelectQuizController controller;
    private final GenerateFromWrongController generateFromWrongController;
    private final CompleteQuizController completeQuizController;

    private final JComboBox<String> categoryBox;
    private final JComboBox<String> difficultyBox;
    private final JSpinner wrongCountSpinner;

    public SelectQuizScreen(JFrame frame,
                            GenerateFromWrongController generateFromWrongController,
                            CompleteQuizController completeQuizController,
                            Player currentPlayer) {
        this.frame = frame;
        this.generateFromWrongController = generateFromWrongController;
        this.completeQuizController = completeQuizController;
        this.currentPlayer = currentPlayer;

        // ✅ Initialize Select Quiz controller
        APIManager apiManager = new APIManager();
        SelectQuizInteractor interactor = new SelectQuizInteractor(apiManager);
        this.controller = new SelectQuizController(interactor);

        setLayout(new BorderLayout(20, 20));
        ThemeUtils.applyGradientBackground(this);

        JLabel title = new JLabel("Select Category and Difficulty", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = ThemeUtils.createGlassPanel(60);
        centerPanel.setLayout(new GridLayout(6, 1, 12, 12));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        JLabel catLabel = new JLabel("Category:");
        ThemeUtils.styleLabel(catLabel, "body");
        String[] categories = {
                "9 - General Knowledge",
                "21 - Sports",
                "23 - History",
                "17 - Science & Nature",
                "11 - Film"
        };
        categoryBox = new JComboBox<>(categories);
        categoryBox.setFont(ThemeUtils.BODY_FONT);

        JLabel diffLabel = new JLabel("Difficulty:");
        ThemeUtils.styleLabel(diffLabel, "body");
        String[] difficulties = {"easy", "medium", "hard"};
        difficultyBox = new JComboBox<>(difficulties);
        difficultyBox.setFont(ThemeUtils.BODY_FONT);

        JLabel wrongLabel = new JLabel("Number of wrong questions to practice:");
        ThemeUtils.styleLabel(wrongLabel, "body");
        wrongCountSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));

        centerPanel.add(catLabel);
        centerPanel.add(categoryBox);
        centerPanel.add(diffLabel);
        centerPanel.add(difficultyBox);
        centerPanel.add(wrongLabel);
        centerPanel.add(wrongCountSpinner);
        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 40, 80));

        JButton backButton = createStyledButton(
                "Back to Home",
                new Color(200, 70, 70),
                new Color(220, 90, 90),
                this::goBackHome
        );
        JButton practiceButton = createStyledButton(
                "Review Mistakes",
                ThemeUtils.MINT,
                ThemeUtils.MINT_HOVER,
                this::onPracticeWrong
        );
        JButton startButton = createStyledButton(
                "Start Quiz",
                ThemeUtils.DEEP_TEAL,
                ThemeUtils.DEEP_TEAL_HOVER,
                this::onStart
        );

        buttonPanel.add(backButton);
        buttonPanel.add(practiceButton);
        buttonPanel.add(startButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color base, Color hover,
                                       java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(ThemeUtils.BUTTON_FONT);
        button.setBackground(base);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
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
     * ✅ CLEAN ARCHITECTURE: Use controller to get questions
     */
    private void onStart(ActionEvent e) {
        try {
            String categoryText = (String) categoryBox.getSelectedItem();
            String categoryId = categoryText.split(" - ")[0].trim();
            String difficulty = (String) difficultyBox.getSelectedItem();

            // ✅ Use controller (not direct API call)
            List<Question> questions = controller.getQuestions(categoryId, difficulty, 5);
            
            if (questions.isEmpty()) {
                JOptionPane.showMessageDialog(
                        frame,
                        "No questions found. Try another combination.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } else {
                frame.getContentPane().removeAll();
                frame.add(new QuizScreen(frame, questions, currentPlayer, 
                        completeQuizController, generateFromWrongController));
                frame.revalidate();
                frame.repaint();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Failed to load questions: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * ✅ CLEAN ARCHITECTURE: Use GenerateFromWrongController
     * The controller will handle all business logic and create the quiz
     */
    private void onPracticeWrong(ActionEvent e) {
        int requested = (int) wrongCountSpinner.getValue();

        // ✅ Call controller - it will:
        // 1. Get wrong questions from DAO
        // 2. Validate available count
        // 3. Create practice quiz
        // 4. Show error or success via presenter
        try {
            generateFromWrongController.generate(currentPlayer.getPlayerName(), requested);
            
            // The presenter will show a dialog with the quiz ID
            // We should navigate to that quiz, but for now this triggers the
            // existing dialog-based flow in GenerateFromWrongPresenter
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Failed to create practice quiz: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void goBackHome(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(
                frame,
                currentPlayer,
                generateFromWrongController,
                completeQuizController,
                null
        ));
        frame.revalidate();
        frame.repaint();
    }
}