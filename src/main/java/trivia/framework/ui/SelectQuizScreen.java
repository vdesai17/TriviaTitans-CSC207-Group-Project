package trivia.framework.ui;

import trivia.entity.Player;
import trivia.entity.Question;
import trivia.interface_adapter.api.APIManager;
import trivia.interface_adapter.controller.SelectQuizController;
import trivia.use_case.select_quiz.SelectQuizInteractor;
import trivia.interface_adapter.controller.GenerateFromWrongController;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SelectQuizScreen extends JPanel {
    private final JFrame frame;
    private final Player currentPlayer;
    private final JComboBox<String> categoryBox;
    private final JComboBox<String> difficultyBox;
    private final SelectQuizController controller;
    private final GenerateFromWrongController generateFromWrongController;
    private final JSpinner wrongCountSpinner;

    public SelectQuizScreen(JFrame frame,
                            GenerateFromWrongController generateFromWrongController,
                            Player currentPlayer) {
        this.frame = frame;
        this.generateFromWrongController = generateFromWrongController;
        this.currentPlayer = currentPlayer;

        // Architecture wiring
        APIManager apiManager = new APIManager();
        SelectQuizInteractor interactor = new SelectQuizInteractor(apiManager);
        this.controller = new SelectQuizController(interactor);

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Select Category and Difficulty", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(60, 150, 60, 150));

        JLabel catLabel = new JLabel("Category:");
        String[] categories = {"9 - General Knowledge", "21 - Sports", "23 - History", "17 - Science & Nature", "11 - Film"};
        categoryBox = new JComboBox<>(categories);

        JLabel diffLabel = new JLabel("Difficulty:");
        String[] difficulties = {"easy", "medium", "hard"};
        difficultyBox = new JComboBox<>(difficulties);

        JButton startButton = new JButton("Start Quiz");
        startButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        startButton.addActionListener(this::onStart);

        JLabel wrongLabel = new JLabel("Number of wrong questions:");
        wrongCountSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));

        JButton practiceWrongButton = new JButton("Practice Wrong Questions");
        practiceWrongButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        practiceWrongButton.addActionListener(this::onPracticeWrong);

        centerPanel.add(catLabel);
        centerPanel.add(categoryBox);
        centerPanel.add(diffLabel);
        centerPanel.add(difficultyBox);
        centerPanel.add(wrongLabel);
        centerPanel.add(wrongCountSpinner);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 150, 20, 150));

        JButton backButton = new JButton("Back to Home");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        backButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.add(new HomeScreen(frame, currentPlayer, generateFromWrongController));
            frame.revalidate();
            frame.repaint();
        });

        bottomPanel.add(backButton);
        bottomPanel.add(practiceWrongButton);
        bottomPanel.add(startButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void onStart(ActionEvent e) {
        try {
            String categoryText = (String) categoryBox.getSelectedItem();
            String categoryId = categoryText.split(" - ")[0].trim();
            String difficulty = (String) difficultyBox.getSelectedItem();

            List<Question> questions = controller.getQuestions(categoryId, difficulty, 5);

            if (questions.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "No questions found. Try another combination.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                frame.getContentPane().removeAll();
                frame.add(new QuizScreen(frame, questions, currentPlayer)); // pass currentPlayer
                frame.revalidate();
                frame.repaint();

            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Failed to load questions: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onPracticeWrong(ActionEvent e) {
        int n = (int) wrongCountSpinner.getValue();
        generateFromWrongController.generate(currentPlayer.getPlayerName(), n);
    }
}
