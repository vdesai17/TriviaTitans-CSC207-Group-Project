package trivia.framework.ui;

import trivia.entity.Player;
import trivia.entity.Question;
import trivia.entity.Quiz;
import trivia.interface_adapter.dao.QuizDataAccessObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateCustomQuizScreen extends JPanel {
    private final JFrame frame;
    private final Player player;
    private final QuizDataAccessObject quizDAO = new QuizDataAccessObject();

    private final JTextField quizTitleField;
    private final JTextField questionField;
    private final JTextField[] optionFields;
    private final JComboBox<String> correctAnswerBox;
    private final DefaultListModel<String> questionListModel;

    private final List<Question> createdQuestions = new ArrayList<>();

    public CreateCustomQuizScreen(JFrame frame, Player player) {
        this.frame = frame;
        this.player = player;
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Create Custom Quiz", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        // Quiz Info
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        topPanel.add(new JLabel("Quiz Title:"));
        quizTitleField = new JTextField();
        topPanel.add(quizTitleField);
        add(topPanel, BorderLayout.NORTH);

        // Question Input
        JPanel centerPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Add a Question"));

        questionField = new JTextField();
        optionFields = new JTextField[4];
        correctAnswerBox = new JComboBox<>(new String[]{"Option 1", "Option 2", "Option 3", "Option 4"});

        centerPanel.add(new JLabel("Question:"));
        centerPanel.add(questionField);

        for (int i = 0; i < 4; i++) {
            optionFields[i] = new JTextField();
            centerPanel.add(new JLabel("Option " + (i + 1) + ":"));
            centerPanel.add(optionFields[i]);
        }

        centerPanel.add(new JLabel("Correct Answer:"));
        centerPanel.add(correctAnswerBox);

        JButton addQuestionButton = new JButton("Add Question");
        addQuestionButton.addActionListener(this::handleAddQuestion);
        centerPanel.add(addQuestionButton);

        add(centerPanel, BorderLayout.CENTER);

        // Sidebar showing added questions
        questionListModel = new DefaultListModel<>();
        JList<String> questionList = new JList<>(questionListModel);
        questionList.setBorder(BorderFactory.createTitledBorder("Added Questions"));
        add(new JScrollPane(questionList), BorderLayout.EAST);

        // Bottom Controls
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save Quiz");
        saveButton.addActionListener(this::handleSaveQuiz);

        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.add(new HomeScreen(frame, player, null));
            frame.revalidate();
            frame.repaint();
        });

        bottomPanel.add(saveButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handleAddQuestion(ActionEvent e) {
        String questionText = questionField.getText().trim();
        List<String> options = new ArrayList<>();
        for (JTextField field : optionFields) options.add(field.getText().trim());
        String correctAnswer = options.get(correctAnswerBox.getSelectedIndex());

        if (questionText.isEmpty() || options.stream().anyMatch(String::isEmpty)) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Question q = new Question(
                UUID.randomUUID().toString(),
                questionText,
                options,
                correctAnswer,
                "Custom",
                "N/A"
        );
        createdQuestions.add(q);
        questionListModel.addElement(questionText);
        clearQuestionFields();
    }

    private void handleSaveQuiz(ActionEvent e) {
        if (quizTitleField.getText().trim().isEmpty() || createdQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please add a title and at least one question.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Quiz customQuiz = new Quiz(
                UUID.randomUUID().toString(),
                quizTitleField.getText().trim(),
                "Custom",
                "N/A",
                player.getPlayerName(),
                createdQuestions
        );

        quizDAO.saveQuiz(customQuiz);
        JOptionPane.showMessageDialog(frame, "Quiz saved successfully!");

        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(frame, player, null));
        frame.revalidate();
        frame.repaint();
    }

    private void clearQuestionFields() {
        questionField.setText("");
        for (JTextField f : optionFields) f.setText("");
        correctAnswerBox.setSelectedIndex(0);
    }
}
