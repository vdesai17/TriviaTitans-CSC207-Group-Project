package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.CreateQuizController;
import trivia.interface_adapter.presenter.CreateQuizViewModel;

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
 * ✅ CLEAN ARCHITECTURE VERSION:
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

    private final JTextField quizTitleField;
    private final JTextField questionField;
    private final JTextField[] optionFields;
    private final JComboBox<String> correctAnswerBox;
    private final DefaultListModel<String> questionListModel;

    // ✅ UI-only data structures (NO use_case InputData here)
    private final List<String> questionTexts = new ArrayList<>();
    private final List<List<String>> optionsList = new ArrayList<>();
    private final List<Integer> correctIndexes = new ArrayList<>();

    public CreateCustomQuizScreen(JFrame frame,
                                  Player player,
                                  CreateQuizController controller,
                                  CreateQuizViewModel viewModel) {
        this.frame = frame;
        this.player = player;
        this.controller = controller;
        this.viewModel = viewModel;

        // ✅ 监听 ViewModel 的变化
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

    /**
     * 收集单个问题，先缓存到本地 List，不直接碰 use_case。
     */
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

        // 只存普通数据，真正的 InputData 在 Controller 里创建
        questionTexts.add(questionText);
        optionsList.add(options);
        correctIndexes.add(correctIdx);

        questionListModel.addElement(questionText);
        clearQuestionFields();
    }

    /**
     * 点击“Save Quiz”时，把普通数据交给 Controller。
     */
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
        // 成功 / 失败的 UI 反馈交给 propertyChange()
    }

    /**
     * ViewModel 被 Presenter 更新时，触发这个方法。
     */
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
        // 离开前取消监听，避免内存泄漏
        viewModel.removePropertyChangeListener(this);

        frame.getContentPane().removeAll();
        // 这里暂时还是用简单构造，你后面可以用 Builder 注入完整 HomeScreen 依赖
        frame.add(new HomeScreen(frame, player, null));
        frame.revalidate();
        frame.repaint();
    }
}
