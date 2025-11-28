package trivia.framework.ui;

import trivia.entity.Player;
import trivia.entity.Question;
import trivia.entity.Quiz;
import trivia.interface_adapter.api.APIManager;
import trivia.interface_adapter.controller.CompleteQuizController;
import trivia.interface_adapter.controller.SelectQuizController;
import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.interface_adapter.dao.QuizDataAccessObject;
import trivia.use_case.generate_from_wrong.WrongQuestionRecord;
import trivia.use_case.select_quiz.SelectQuizInteractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * SelectQuizScreen — choose quiz category, difficulty, or practice from wrong questions.
 * Styled with unified dark-teal gradient and glass UI design.
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

    /** Creates a unified styled button with hover transition */
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

    private void onStart(ActionEvent e) {
        try {
            String categoryText = (String) categoryBox.getSelectedItem();
            String categoryId = categoryText.split(" - ")[0].trim();
            String difficulty = (String) difficultyBox.getSelectedItem();

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
                frame.add(new QuizScreen(frame, questions, currentPlayer, completeQuizController));
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

    private void onPracticeWrong(ActionEvent e) {
        int requested = (int) wrongCountSpinner.getValue();

        QuizDataAccessObject quizDAO = new QuizDataAccessObject();

        List<WrongQuestionRecord> allWrong =
                quizDAO.getWrongQuestionsForPlayer(currentPlayer.getPlayerName());

        if (allWrong == null || allWrong.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "You don't have any recorded wrong questions yet.\n" +
                            "Please finish at least one quiz first.",
                    "No Wrong Questions",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        List<WrongQuestionRecord> distinctWrong = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (WrongQuestionRecord r : allWrong) {
            String key =
                    r.getQuestionText() + "||" +
                            r.getCorrectAnswer() + "||" +
                            String.join("||", r.getOptions());

            if (seen.add(key)) {
                distinctWrong.add(r);
            }
        }

        int available = distinctWrong.size();

        if (requested > available) {
            JOptionPane.showMessageDialog(
                    frame,
                    "You currently have only " + available + " wrong questions available.\n" +
                            "Please request at most " + available + " questions.",
                    "Not Enough Wrong Questions",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        List<WrongQuestionRecord> subset =
                new ArrayList<>(distinctWrong.subList(0, requested));

        String quizId = quizDAO.createQuizFromWrongQuestions(
                currentPlayer.getPlayerName(),
                subset
        );

        Quiz practiceQuiz = quizDAO.getQuizById(quizId);
        if (practiceQuiz == null) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Failed to create practice quiz from wrong questions.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        frame.getContentPane().removeAll();
        frame.add(new QuizScreen(frame, practiceQuiz.getQuestions(), currentPlayer, completeQuizController));
        frame.revalidate();
        frame.repaint();
    }

    private void goBackHome(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(
                frame,
                currentPlayer,
                generateFromWrongController,
                completeQuizController,
                new QuizDataAccessObject()
        ));
        frame.revalidate();
        frame.repaint();
    }
}
