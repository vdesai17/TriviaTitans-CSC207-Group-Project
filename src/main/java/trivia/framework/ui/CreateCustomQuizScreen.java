package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.CreateQuizController;
import trivia.interface_adapter.presenter.CreateQuizViewModel;
import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.interface_adapter.controller.CompleteQuizController;
import trivia.interface_adapter.dao.QuizDataAccessObject;
import trivia.interface_adapter.presenter.GenerateFromWrongViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * CreateCustomQuizScreen — allows the user to build and save a custom quiz.
 *
 * - NO direct DAO access
 * - Does NOT use use_case InputData classes
 * - Talks only to CreateQuizController and CreateQuizViewModel
 * - UI logic only (collecting form data + basic empty-field checks)
 */
public class CreateCustomQuizScreen extends JPanel implements PropertyChangeListener {
    private final JFrame frame;
    private final Player player;
    private final CreateQuizController controller;
    private final CreateQuizViewModel viewModel;

    private final GenerateFromWrongController generateFromWrongController;
    private final CompleteQuizController completeQuizController;
    private final QuizDataAccessObject quizDAO;
    private final GenerateFromWrongViewModel generateFromWrongViewModel;

    private final JTextField quizTitleField;
    private final JTextField questionField;
    private final JTextField[] optionFields;
    private final JComboBox<String> correctAnswerBox;
    private final DefaultListModel<String> questionListModel;

    private final List<String> questionTexts = new ArrayList<>();
    private final List<List<String>> optionsList = new ArrayList<>();
    private final List<Integer> correctIndexes = new ArrayList<>();

    public CreateCustomQuizScreen(JFrame frame,
                                  Player player,
                                  CreateQuizController controller,
                                  CreateQuizViewModel viewModel,
                                  GenerateFromWrongController generateFromWrongController,
                                  CompleteQuizController completeQuizController,
                                  QuizDataAccessObject quizDAO,
                                  GenerateFromWrongViewModel generateFromWrongViewModel) {
        this.frame = frame;
        this.player = player;
        this.controller = controller;
        this.viewModel = viewModel;

        this.generateFromWrongController = generateFromWrongController;
        this.completeQuizController = completeQuizController;
        this.quizDAO = quizDAO;
        this.generateFromWrongViewModel = generateFromWrongViewModel;


        this.viewModel.addPropertyChangeListener(this);

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

        // Question label
        JLabel questionLabel = new JLabel("Question:");
        ThemeUtils.styleLabel(questionLabel, "body");
        gbc.gridy++;
        centerPanel.add(questionLabel, gbc);

        // Question input
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

        // Correct Answer selector
        gbc.gridy++;
        JLabel correctLabel = new JLabel("Correct Answer:");
        ThemeUtils.styleLabel(correctLabel, "body");
        centerPanel.add(correctLabel, gbc);

        gbc.gridy++;
        correctAnswerBox = new JComboBox<>(new String[]{"Option 1", "Option 2", "Option 3", "Option 4"});
        correctAnswerBox.setFont(ThemeUtils.BODY_FONT);
        centerPanel.add(correctAnswerBox, gbc);

        // Add Question button
        gbc.gridy++;
        JButton addQuestionButton = ThemeUtils.createStyledButton(
                "Add Question",
                ThemeUtils.MINT,
                ThemeUtils.MINT_HOVER
        );
        addQuestionButton.addActionListener(this::handleAddQuestion);
        centerPanel.add(addQuestionButton, gbc);

        // Scroll wrapper
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

        JButton saveButton = ThemeUtils.createStyledButton(
                "Save Quiz",
                ThemeUtils.DEEP_TEAL,
                ThemeUtils.DEEP_TEAL_HOVER
        );
        saveButton.addActionListener(this::handleSaveQuiz);

        JButton backButton = ThemeUtils.createStyledButton(
                "Back to Home",
                new Color(180, 60, 60),
                new Color(210, 80, 80)
        );
        backButton.addActionListener(e -> navigateToHome());

        bottomPanel.add(saveButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handleAddQuestion(ActionEvent e) {
        String questionText = questionField.getText().trim();
        List<String> options = new ArrayList<>();
        for (JTextField field : optionFields) {
            options.add(field.getText().trim());
        }

        // UI-level validation: ensure fields are filled
        if (questionText.isEmpty() || options.stream().anyMatch(String::isEmpty)) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please fill in all fields.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int correctIdx = correctAnswerBox.getSelectedIndex();
        if (correctIdx < 0 || correctIdx >= options.size()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please select a valid correct answer.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        questionTexts.add(questionText);
        optionsList.add(options);
        correctIndexes.add(correctIdx);

        questionListModel.addElement(questionText);
        clearQuestionFields();
    }

    private void handleSaveQuiz(ActionEvent e) {
        String title = quizTitleField.getText().trim();

        // Basic UI-level validation
        if (title.isEmpty() || questionTexts.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please add a title and at least one question.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        controller.execute(
                title,
                player.getPlayerName(),            // creator name
                "Custom",                          // category
                "N/A",                             // difficulty
                new ArrayList<>(questionTexts),
                new ArrayList<>(optionsList),
                new ArrayList<>(correctIndexes)
        );
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!CreateQuizViewModel.CREATE_QUIZ_PROPERTY.equals(evt.getPropertyName())) {
            return;
        }

        if (viewModel.isSuccess()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Quiz saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            navigateToHome();
        } else {
            String errorMessage = viewModel.getErrorMessage();
            if (errorMessage != null && !errorMessage.isEmpty()) {
                JOptionPane.showMessageDialog(
                        frame,
                        errorMessage,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void clearQuestionFields() {
        questionField.setText("");
        for (JTextField f : optionFields) {
            f.setText("");
        }
        correctAnswerBox.setSelectedIndex(0);
    }

    private void navigateToHome() {
        viewModel.removePropertyChangeListener(this);

        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(
                frame,
                player,
                generateFromWrongController,
                completeQuizController,
                quizDAO,
                generateFromWrongViewModel
        ));
        frame.revalidate();
        frame.repaint();
    }
}
