package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.CreateQuizController;
import trivia.interface_adapter.presenter.CreateQuizViewModel;
import trivia.use_case.create_quiz.AddQuestionInputData;

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
 * ✅ REFACTORED TO FOLLOW CLEAN ARCHITECTURE:
 * - NO direct DAO access
 * - Uses CreateQuizController (not DAO)
 * - Observes CreateQuizViewModel (via PropertyChangeListener)
 * - UI logic only - no business logic
 */
public class CreateCustomQuizScreen extends JPanel implements PropertyChangeListener {
    private final JFrame frame;
    private final Player player;
    private final CreateQuizController controller;
    private final CreateQuizViewModel viewModel;

    private final JTextField quizTitleField;
    private final JTextField questionField;
    private final JTextField[] optionFields;
    private final JComboBox<String> correctAnswerBox;
    private final DefaultListModel<String> questionListModel;
    private final List<AddQuestionInputData> createdQuestions = new ArrayList<>();

    /**
     * Constructor with proper dependency injection
     * 
     * @param frame The main application frame
     * @param player The current player
     * @param controller The CreateQuiz controller (injected)
     * @param viewModel The CreateQuiz ViewModel (injected)
     */
    public CreateCustomQuizScreen(JFrame frame, Player player,
                                  CreateQuizController controller,
                                  CreateQuizViewModel viewModel) {
        this.frame = frame;
        this.player = player;
        this.controller = controller;
        this.viewModel = viewModel;
        
        // ✅ CLEAN ARCHITECTURE: Listen to ViewModel changes
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
        backButton.addActionListener(e -> navigateToHome());

        bottomPanel.add(saveButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * ✅ CLEAN ARCHITECTURE: This is UI logic only - validation happens in use case
     */
    private void handleAddQuestion(ActionEvent e) {
        String questionText = questionField.getText().trim();
        List<String> options = new ArrayList<>();
        for (JTextField field : optionFields) {
            options.add(field.getText().trim());
        }
        String correctAnswer = options.get(correctAnswerBox.getSelectedIndex());

        // Basic UI-level validation (empty fields)
        if (questionText.isEmpty() || options.stream().anyMatch(String::isEmpty)) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ Create input data for use case (no Quiz ID needed here - temporary storage)
        AddQuestionInputData questionData = new AddQuestionInputData(
                null,  // quizId will be assigned when quiz is created
                questionText,
                options,
                correctAnswer,
                "Custom",  // category
                "N/A"      // difficulty
        );

        createdQuestions.add(questionData);
        questionListModel.addElement(questionText);
        clearQuestionFields();
    }

    /**
     * ✅ CLEAN ARCHITECTURE: Calls controller, not DAO directly
     */
    private void handleSaveQuiz(ActionEvent e) {
        String title = quizTitleField.getText().trim();

        // Basic UI-level validation
        if (title.isEmpty() || createdQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Please add a title and at least one question.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ CLEAN ARCHITECTURE: Call controller (not DAO)
        // The use case will handle all business logic and data persistence
        controller.execute(
            title,
            player.getPlayerName(),  // creator name
            "Custom",                 // category
            "N/A",                    // difficulty
            new ArrayList<>(createdQuestions)  // questions
        );
        
        // ✅ Don't show success message here - wait for ViewModel update
        // The propertyChange() method will handle UI updates
    }

    /**
     * ✅ CLEAN ARCHITECTURE: React to ViewModel changes (observer pattern)
     * This is called by the ViewModel when the Presenter updates it
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(CreateQuizViewModel.CREATE_QUIZ_PROPERTY)) {
            if (viewModel.isSuccess()) {
                // ✅ Success: Show message and navigate
                JOptionPane.showMessageDialog(frame, 
                    "Quiz saved successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                navigateToHome();
            } else {
                // ✅ Failure: Show error message from use case
                String errorMessage = viewModel.getErrorMessage();
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, 
                        errorMessage, 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void clearQuestionFields() {
        questionField.setText("");
        for (JTextField f : optionFields) f.setText("");
        correctAnswerBox.setSelectedIndex(0);
    }

    private void navigateToHome() {
        // Clean up listener before leaving
        viewModel.removePropertyChangeListener(this);
        
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(frame, player, null));
        frame.revalidate();
        frame.repaint();
    }
}