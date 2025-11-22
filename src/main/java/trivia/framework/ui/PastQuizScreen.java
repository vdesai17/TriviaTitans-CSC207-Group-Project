package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.interface_adapter.controller.ReviewController;
import trivia.interface_adapter.presenter.PastQuizViewModel;
import trivia.use_case.review_quiz.ReviewQuizRequestModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * UI Screen for viewing and editing past quiz attempts (Use Case 3)
 */
public class PastQuizScreen extends JPanel implements PropertyChangeListener {
    
    private final JFrame frame;
    private final ReviewController controller;
    private final PastQuizViewModel viewModel;
    private final Player currentPlayer;
    private final GenerateFromWrongController generateFromWrongController;

    // UI Components
    private JList<String> pastQuizzesList;
    private DefaultListModel<String> listModel;
    private JPanel questionsPanel;
    private JButton saveButton;
    private JButton backButton;
    private JLabel messageLabel;
    private List<ButtonGroup> answerGroups;  // One ButtonGroup per question

    public PastQuizScreen(JFrame frame, ReviewController controller, 
                         PastQuizViewModel viewModel, Player currentPlayer,
                         GenerateFromWrongController generateFromWrongController) {
        this.frame = frame;
        this.controller = controller;
        this.viewModel = viewModel;
        this.currentPlayer = currentPlayer;
        this.generateFromWrongController = generateFromWrongController;

        // Listen for ViewModel changes
        viewModel.addPropertyChangeListener(this);

        initComponents();

        // Load past quizzes when screen is created
        controller.viewPastQuizzes(currentPlayer.getPlayerName());
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("Past Quizzes - Review & Edit", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        // Left panel: List of past quizzes
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Your Quiz History"));
        
        listModel = new DefaultListModel<>();
        pastQuizzesList = new JList<>(listModel);
        pastQuizzesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pastQuizzesList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pastQuizzesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onPastQuizSelected();
            }
        });
        JScrollPane listScroll = new JScrollPane(pastQuizzesList);
        leftPanel.add(listScroll, BorderLayout.CENTER);

        // Right panel: Questions display
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Quiz Details"));
        
        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setBackground(Color.WHITE);
        JScrollPane questionsScroll = new JScrollPane(questionsPanel);
        rightPanel.add(questionsScroll, BorderLayout.CENTER);

        // Message label
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        messageLabel.setForeground(new Color(50, 100, 200));

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        
        saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(this::onSaveClicked);
        saveButton.setEnabled(false);

        backButton = new JButton("Back to Home");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        backButton.addActionListener(e -> navigateToHome());

        bottomPanel.add(saveButton);
        bottomPanel.add(backButton);

        // Add to main panel
        add(title, BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                                              leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);
        
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(messageLabel, BorderLayout.NORTH);
        bottomContainer.add(bottomPanel, BorderLayout.SOUTH);
        add(bottomContainer, BorderLayout.SOUTH);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Called automatically when ViewModel changes
        refreshFromViewModel();
    }

    private void refreshFromViewModel() {
        SwingUtilities.invokeLater(() -> {
            // 1. Update past quizzes list
            updatePastQuizzesList();

            // 2. Update questions panel
            updateQuestionsPanel();

            // 3. Enable/disable Save button
            saveButton.setEnabled(viewModel.isEditingEnabled());

            // 4. Show message
            String message = viewModel.getMessage();
            if (message != null && !message.isEmpty()) {
                messageLabel.setText(message);
            } else {
                messageLabel.setText(" ");
            }
        });
    }

    private void updatePastQuizzesList() {
        listModel.clear();
        List<PastQuizViewModel.PastQuizSummaryViewModel> quizzes = viewModel.getPastQuizzes();
        
        if (quizzes != null && !quizzes.isEmpty()) {
            for (PastQuizViewModel.PastQuizSummaryViewModel summary : quizzes) {
                listModel.addElement(summary.toString());
            }
        } else {
            listModel.addElement("(No past quizzes found)");
        }
    }

    private void updateQuestionsPanel() {
        questionsPanel.removeAll();
        answerGroups = new ArrayList<>();

        List<PastQuizViewModel.QuestionRowViewModel> questions = viewModel.getQuestions();
        
        if (questions == null || questions.isEmpty()) {
            JLabel placeholder = new JLabel("← Select a quiz from the list to view details");
            placeholder.setFont(new Font("SansSerif", Font.ITALIC, 16));
            placeholder.setForeground(Color.GRAY);
            questionsPanel.add(placeholder);
        } else {
            // Show quiz title
            JLabel quizTitleLabel = new JLabel("Quiz: " + viewModel.getQuizTitle());
            quizTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            quizTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            questionsPanel.add(quizTitleLabel);
            questionsPanel.add(Box.createVerticalStrut(15));

            // Display each question
            for (int i = 0; i < questions.size(); i++) {
                PastQuizViewModel.QuestionRowViewModel question = questions.get(i);
                JPanel questionPanel = createQuestionPanel(i + 1, question);
                questionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                questionsPanel.add(questionPanel);
                questionsPanel.add(Box.createVerticalStrut(15));
            }
        }

        questionsPanel.revalidate();
        questionsPanel.repaint();
    }

    private JPanel createQuestionPanel(int questionNumber, 
                                      PastQuizViewModel.QuestionRowViewModel question) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setBackground(Color.WHITE);

        // Question text
        JLabel questionLabel = new JLabel(
                String.format("<html><b>Q%d:</b> %s</html>", 
                questionNumber, question.getQuestionText()));
        questionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(questionLabel);
        panel.add(Box.createVerticalStrut(10));

        // Options as radio buttons
        ButtonGroup group = new ButtonGroup();
        answerGroups.add(group);

        List<String> options = question.getOptions();
        int correctIndex = question.getCorrectIndex();
        int selectedIndex = question.getSelectedIndex();

        for (int i = 0; i < options.size(); i++) {
            JRadioButton radioButton = new JRadioButton(options.get(i));
            radioButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
            radioButton.setBackground(Color.WHITE);
            radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Mark correct answer in green
            if (i == correctIndex) {
                radioButton.setForeground(new Color(0, 150, 0));
                radioButton.setText(radioButton.getText() + " ✓");
                radioButton.setFont(new Font("SansSerif", Font.BOLD, 13));
            }
            
            // Mark incorrect selection in red
            if (i == selectedIndex && i != correctIndex) {
                radioButton.setForeground(new Color(200, 0, 0));
                radioButton.setFont(new Font("SansSerif", Font.BOLD, 13));
            }

            // Pre-select the user's answer
            if (i == selectedIndex) {
                radioButton.setSelected(true);
            }

            // Enable/disable based on editing permission
            radioButton.setEnabled(viewModel.isEditingEnabled());

            group.add(radioButton);
            panel.add(radioButton);
        }

        return panel;
    }

    private void onPastQuizSelected() {
        int selectedIndex = pastQuizzesList.getSelectedIndex();
        if (selectedIndex >= 0) {
            List<PastQuizViewModel.PastQuizSummaryViewModel> quizzes = viewModel.getPastQuizzes();
            if (quizzes != null && selectedIndex < quizzes.size()) {
                String attemptId = quizzes.get(selectedIndex).getAttemptId();
                controller.openAttempt(attemptId);
            }
        }
    }

    private void onSaveClicked(ActionEvent e) {
        String attemptId = viewModel.getCurrentAttemptId();
        if (attemptId == null) {
            JOptionPane.showMessageDialog(this, "No quiz selected", "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Integer> updatedIndices = readSelectedIndicesFromUI();
        
        // Create request model with proper structure
        ReviewQuizRequestModel requestModel = new ReviewQuizRequestModel(
                attemptId, currentPlayer.getPlayerName(), updatedIndices);
        
        controller.saveEditedAnswers(requestModel);
        
        JOptionPane.showMessageDialog(this, 
                "Changes saved successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
    }

    private List<Integer> readSelectedIndicesFromUI() {
        List<Integer> indices = new ArrayList<>();
        
        for (ButtonGroup group : answerGroups) {
            int selectedIndex = -1;
            int buttonIndex = 0;
            
            for (var button : java.util.Collections.list(group.getElements())) {
                if (button.isSelected()) {
                    selectedIndex = buttonIndex;
                    break;
                }
                buttonIndex++;
            }
            
            indices.add(selectedIndex);
        }
        
        return indices;
    }

    private void navigateToHome() {
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(frame, currentPlayer, generateFromWrongController));
        frame.revalidate();
        frame.repaint();
    }
}