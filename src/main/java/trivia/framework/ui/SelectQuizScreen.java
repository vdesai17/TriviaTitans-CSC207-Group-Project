package trivia.framework.ui;

import trivia.entity.Question;
import trivia.interface_adapter.api.APIManager;
import trivia.interface_adapter.controller.SelectQuizController;
import trivia.use_case.select_quiz.SelectQuizInteractor;

// for UC6
import trivia.interface_adapter.controller.GenerateFromWrongController;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SelectQuizScreen extends JPanel {
    private final JFrame frame;
    private final JComboBox<String> categoryBox;
    private final JComboBox<String> difficultyBox;
    private final SelectQuizController controller;

    // for UC6
    private final GenerateFromWrongController generateFromWrongController;
    private final JSpinner wrongCountSpinner;
    private final JButton practiceWrongButton;

    public SelectQuizScreen(JFrame frame,
                            GenerateFromWrongController generateFromWrongController) {
        this.frame = frame;
        this.generateFromWrongController = generateFromWrongController;

        // Clean-architecture wiring
        APIManager apiManager = new APIManager();
        SelectQuizInteractor interactor = new SelectQuizInteractor(apiManager);
        this.controller = new SelectQuizController(interactor);

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Select Category and Difficulty", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(100, 150, 100, 150));

        JLabel catLabel = new JLabel("Category:");
        String[] categories = {
                "9 - General Knowledge", "21 - Sports",
                "23 - History", "17 - Science & Nature", "11 - Film"
        };
        categoryBox = new JComboBox<>(categories);

        JLabel diffLabel = new JLabel("Difficulty:");
        String[] difficulties = {"easy", "medium", "hard"};
        difficultyBox = new JComboBox<>(difficulties);

        JButton startButton = new JButton("Load Questions");
        startButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        startButton.addActionListener(this::onStart);

        JLabel wrongLabel = new JLabel("Number of wrong questions:");
        wrongCountSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));

        practiceWrongButton = new JButton("Practice Wrong Questions");
        practiceWrongButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        practiceWrongButton.addActionListener(this::onPracticeWrong);

        centerPanel.add(catLabel);
        centerPanel.add(categoryBox);
        centerPanel.add(diffLabel);
        centerPanel.add(difficultyBox);
        centerPanel.add(wrongLabel);
        centerPanel.add(wrongCountSpinner);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));
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
                frame.add(new QuizScreen(frame, questions));
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

        String playerName = JOptionPane.showInputDialog(frame,
                "Enter your name:", "Player Name",
                JOptionPane.PLAIN_MESSAGE);

        if (playerName == null || playerName.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Player name required.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        generateFromWrongController.generate(playerName, n);
    }
}
