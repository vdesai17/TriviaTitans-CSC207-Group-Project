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

/**
 * CreateCustomQuizScreen — allows the user to build and save a custom quiz.
 * Uses the unified dark-teal gradient theme with glass panels and modern buttons.
 */
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

        setLayout(new BorderLayout(20, 20));
        ThemeUtils.applyGradientBackground(this);

        // --- Header ---
        JLabel title = new JLabel("Create Custom Quiz", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        add(title, BorderLayout.NORTH);

        // --- Center Input Panel (Glass Effect) ---
        JPanel centerPanel = ThemeUtils.createGlassPanel(60);
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Quiz Title
        JLabel quizTitleLabel = new JLabel("Quiz Title:");
        ThemeUtils.styleLabel(quizTitleLabel, "body");
        centerPanel.add(quizTitleLabel, gbc);

        gbc.gridy++;
        quizTitleField = new JTextField(20);
        quizTitleField.setFont(ThemeUtils.BODY_FONT);
        quizTitleField.setBackground(new Color(255, 255, 255, 220));
        quizTitleField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        centerPanel.add(quizTitleField, gbc);

        gbc.gridy++;
        centerPanel.add(Box.createVerticalStrut(20), gbc);

        // Question
        JLabel questionLabel = new JLabel("Question:");
        ThemeUtils.styleLabel(questionLabel, "body");
        gbc.gridy++;
        centerPanel.add(questionLabel, gbc);

        gbc.gridy++;
        questionField = new JTextField(25);
        questionField.setFont(ThemeUtils.BODY_FONT);
        questionField.setBackground(new Color(255, 255, 255, 220));
        questionField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        centerPanel.add(questionField, gbc);

        // Options
        optionFields = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            gbc.gridy++;
            JLabel optLabel = new JLabel("Option " + (i + 1) + ":");
            ThemeUtils.styleLabel(optLabel, "body");
            centerPanel.add(optLabel, gbc);

            gbc.gridy++;
            optionFields[i] = new JTextField(20);
            optionFields[i].setFont(ThemeUtils.BODY_FONT);
            optionFields[i].setBackground(new Color(255, 255, 255, 220));
            optionFields[i].setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            centerPanel.add(optionFields[i], gbc);
        }

        gbc.gridy++;
        JLabel correctLabel = new JLabel("Correct Answer:");
        ThemeUtils.styleLabel(correctLabel, "body");
        centerPanel.add(correctLabel, gbc);

        gbc.gridy++;
        correctAnswerBox = new JComboBox<>(new String[]{"Option 1", "Option 2", "Option 3", "Option 4"});
        correctAnswerBox.setFont(ThemeUtils.BODY_FONT);
        centerPanel.add(correctAnswerBox, gbc);

        gbc.gridy++;
        JButton addQuestionButton = ThemeUtils.createStyledButton("Add Question", ThemeUtils.MINT, ThemeUtils.MINT_HOVER);
        addQuestionButton.addActionListener(this::handleAddQuestion);
        centerPanel.add(addQuestionButton, gbc);

        // scroll
        JScrollPane centerScrollPane = new JScrollPane(
                centerPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        centerScrollPane.setBorder(null);
        centerScrollPane.getViewport().setOpaque(false);
        centerScrollPane.setOpaque(false);

        add(centerScrollPane, BorderLayout.CENTER);


        // --- Right Sidebar (Added Questions List) ---
        questionListModel = new DefaultListModel<>();
        JList<String> questionList = new JList<>(questionListModel);
        questionList.setFont(ThemeUtils.BODY_FONT);
        questionList.setBorder(BorderFactory.createTitledBorder("Added Questions"));
        questionList.setBackground(new Color(255, 255, 255, 230));
        add(new JScrollPane(questionList), BorderLayout.EAST);

        // --- Bottom Buttons ---
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setOpaque(false);

        JButton saveButton = ThemeUtils.createStyledButton("Save Quiz", ThemeUtils.DEEP_TEAL, ThemeUtils.DEEP_TEAL_HOVER);
        saveButton.addActionListener(this::handleSaveQuiz);

        JButton backButton = ThemeUtils.createStyledButton("Back to Home", new Color(180, 60, 60), new Color(210, 80, 80));
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

    /** Handles adding a question to the temporary list */
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

    /** Saves the quiz to the DAO */
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
        JOptionPane.showMessageDialog(frame, "Quiz saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

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
