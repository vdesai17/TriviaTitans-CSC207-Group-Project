package trivia.framework.ui;

import trivia.entity.Question;
import trivia.interface_adapter.api.APIManager;
import trivia.interface_adapter.controller.SelectQuizController;
import trivia.use_case.select_quiz.SelectQuizInteractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SelectQuizScreen extends JPanel {
    private final JFrame frame;
    private final JComboBox<String> categoryBox;
    private final JComboBox<String> difficultyBox;
    private final SelectQuizController controller;

    public SelectQuizScreen(JFrame frame) {
        this.frame = frame;

        // Clean-architecture wiring
        APIManager apiManager = new APIManager();
        SelectQuizInteractor interactor = new SelectQuizInteractor(apiManager);
        this.controller = new SelectQuizController(interactor);

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Select Category and Difficulty", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 10));
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

        centerPanel.add(catLabel);
        centerPanel.add(categoryBox);
        centerPanel.add(diffLabel);
        centerPanel.add(difficultyBox);

        add(centerPanel, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);
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

                // TODO: move to QuizScreen next
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Failed to load questions: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
