package trivia.framework.ui;

import trivia.entity.Player;
import trivia.entity.Quiz;
import trivia.entity.Question;
import trivia.entity.QuizAttempt;
import trivia.framework.AppFactory;
import trivia.interface_adapter.controller.ReviewController;
import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.interface_adapter.controller.CompleteQuizController;
import trivia.interface_adapter.presenter.PastQuizViewModel;
import trivia.interface_adapter.presenter.GenerateFromWrongViewModel;
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
 * Now with REDO functionality!
 * 
 * CLEAN ARCHITECTURE: Uses AppFactory for DAO access during navigation.
 */
public class PastQuizScreen extends JPanel implements PropertyChangeListener {

    private final JFrame frame;
    private final ReviewController controller;
    private final PastQuizViewModel viewModel;
    private final Player currentPlayer;
    private final GenerateFromWrongController generateFromWrongController;
    private final CompleteQuizController completeQuizController;
    private final GenerateFromWrongViewModel generateFromWrongViewModel;

    private JList<String> pastQuizzesList;
    private DefaultListModel<String> listModel;
    private JPanel questionsPanel;
    private JButton saveButton;
    private JButton redoButton;
    private JButton backButton;
    private JLabel messageLabel;
    private List<ButtonGroup> answerGroups;

    // ✅ REMOVED: private Quiz currentQuiz; 
    // Quiz is now stored in ViewModel following Clean Architecture

    public PastQuizScreen(JFrame frame,
                          ReviewController controller,
                          PastQuizViewModel viewModel,
                          Player currentPlayer,
                          GenerateFromWrongController generateFromWrongController,
                          CompleteQuizController completeQuizController,
                          GenerateFromWrongViewModel generateFromWrongViewModel) {
        this.frame = frame;
        this.controller = controller;
        this.viewModel = viewModel;
        this.currentPlayer = currentPlayer;
        this.generateFromWrongController = generateFromWrongController;
        this.completeQuizController = completeQuizController;
        this.generateFromWrongViewModel = generateFromWrongViewModel;

        viewModel.addPropertyChangeListener(this);

        initComponents();

        controller.viewPastQuizzes(currentPlayer.getPlayerName());
    }

    public PastQuizScreen(JFrame frame,
                          ReviewController controller,
                          PastQuizViewModel viewModel,
                          Player currentPlayer,
                          GenerateFromWrongController generateFromWrongController) {
        this(frame, controller, viewModel, currentPlayer, generateFromWrongController, null, null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ThemeUtils.applyGradientBackground(this);

        JLabel title = new JLabel("Past Quizzes - Review & Redo", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 100, 100), 2),
                "Your Quiz History",
                0, 0,
                ThemeUtils.BODY_FONT,
                new Color(0, 100, 100)
        ));

        listModel = new DefaultListModel<>();
        pastQuizzesList = new JList<>(listModel);
        pastQuizzesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pastQuizzesList.setFont(ThemeUtils.BODY_FONT);
        pastQuizzesList.setBackground(new Color(255, 255, 255, 230));
        pastQuizzesList.setSelectionBackground(new Color(0, 150, 150, 100));
        pastQuizzesList.setSelectionForeground(Color.BLACK);
        pastQuizzesList.setFixedCellHeight(40);
        pastQuizzesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onPastQuizSelected();
            }
        });
        JScrollPane listScroll = new JScrollPane(pastQuizzesList);
        listScroll.setOpaque(false);
        listScroll.getViewport().setOpaque(false);
        leftPanel.add(listScroll, BorderLayout.CENTER);

        JPanel rightPanel = ThemeUtils.createGlassPanel(40);
        rightPanel.setLayout(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 100, 100), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setOpaque(false);
        JScrollPane questionsScroll = new JScrollPane(questionsPanel);
        questionsScroll.setOpaque(false);
        questionsScroll.getViewport().setOpaque(false);
        questionsScroll.setBorder(null);
        rightPanel.add(questionsScroll, BorderLayout.CENTER);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        messageLabel.setForeground(new Color(50, 100, 200));

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setOpaque(false);

        saveButton = ThemeUtils.createStyledButton("Save Changes",
                new Color(76, 175, 80),
                new Color(100, 200, 100));
        saveButton.addActionListener(this::onSaveClicked);
        saveButton.setEnabled(false);

        redoButton = ThemeUtils.createStyledButton("Redo This Quiz",
                ThemeUtils.MINT,
                ThemeUtils.MINT_HOVER);
        redoButton.addActionListener(this::onRedoClicked);
        redoButton.setEnabled(false);

        backButton = ThemeUtils.createStyledButton("Back to Home",
                new Color(180, 60, 60),
                new Color(210, 80, 80));
        backButton.addActionListener(e -> navigateToHome());

        bottomPanel.add(saveButton);
        bottomPanel.add(redoButton);
        bottomPanel.add(backButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        splitPane.setOpaque(false);
        add(splitPane, BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setOpaque(false);
        bottomContainer.add(messageLabel, BorderLayout.NORTH);
        bottomContainer.add(bottomPanel, BorderLayout.SOUTH);
        add(bottomContainer, BorderLayout.SOUTH);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // ✅ CLEAN ARCHITECTURE FIX: Check if this is a redo quiz event
        if ("quizToRedo".equals(evt.getPropertyName())) {
            Quiz quizToRedo = viewModel.getQuizToRedo();
            if (quizToRedo != null) {
                handleRedoQuizNavigation(quizToRedo);
                return;
            }
        }
        
        // Regular UI refresh
        refreshFromViewModel();
    }

    private void refreshFromViewModel() {
        SwingUtilities.invokeLater(() -> {
            updatePastQuizzesList();
            updateQuestionsPanel();
            saveButton.setEnabled(viewModel.isEditingEnabled());
            
            // ✅ CLEAN ARCHITECTURE FIX: Use ViewModel instead of currentQuiz
            redoButton.setEnabled(viewModel.getCurrentAttemptId() != null);

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
            placeholder.setForeground(new Color(100, 100, 100));
            questionsPanel.add(placeholder);
        } else {
            JLabel quizTitleLabel = new JLabel("Quiz: " + viewModel.getQuizTitle());
            quizTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            quizTitleLabel.setForeground(new Color(0, 80, 80));
            quizTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            questionsPanel.add(quizTitleLabel);
            questionsPanel.add(Box.createVerticalStrut(15));

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
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setBackground(new Color(255, 255, 255, 200));

        JLabel questionLabel = new JLabel(
                String.format("<html><b>Q%d:</b> %s</html>",
                        questionNumber, question.getQuestionText()));
        questionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(questionLabel);
        panel.add(Box.createVerticalStrut(10));

        ButtonGroup group = new ButtonGroup();
        answerGroups.add(group);

        List<String> options = question.getOptions();
        int correctIndex = question.getCorrectIndex();
        int selectedIndex = question.getSelectedIndex();

        for (int i = 0; i < options.size(); i++) {
            JRadioButton radioButton = new JRadioButton(options.get(i));
            radioButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
            radioButton.setBackground(new Color(255, 255, 255, 0));
            radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (i == correctIndex) {
                radioButton.setForeground(new Color(0, 150, 0));
                radioButton.setText(radioButton.getText() + " ✓");
                radioButton.setFont(new Font("SansSerif", Font.BOLD, 13));
            }

            if (i == selectedIndex && i != correctIndex) {
                radioButton.setForeground(new Color(200, 0, 0));
                radioButton.setFont(new Font("SansSerif", Font.BOLD, 13));
            }

            if (i == selectedIndex) {
                radioButton.setSelected(true);
            }

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
                
                // ✅ CLEAN ARCHITECTURE: Use controller for all data access
                controller.openAttempt(attemptId);
                
                // ✅ REMOVED: loadQuizForRedo(attemptId);
                // Quiz data now flows through: Controller → Interactor → ViewModel
            }
        }
    }

    // ✅ REMOVED: loadQuizForRedo() method
    // This method violated Clean Architecture by directly instantiating DAO
    // Quiz data now properly flows through the use case layer

    private void onSaveClicked(ActionEvent e) {
        String attemptId = viewModel.getCurrentAttemptId();
        if (attemptId == null) {
            JOptionPane.showMessageDialog(this, "No quiz selected", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Integer> updatedIndices = readSelectedIndicesFromUI();

        ReviewQuizRequestModel requestModel = new ReviewQuizRequestModel(
                attemptId, currentPlayer.getPlayerName(), updatedIndices);

        controller.saveEditedAnswers(requestModel);

        JOptionPane.showMessageDialog(this,
                "Changes saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * ✅ CLEAN ARCHITECTURE FIX: Redo now goes through proper layers
     * Controller → Interactor → Presenter → ViewModel → UI (via propertyChange)
     */
    private void onRedoClicked(ActionEvent e) {
        String attemptId = viewModel.getCurrentAttemptId();
        
        if (attemptId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a quiz to redo.",
                    "No Quiz Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to redo this quiz?\n" +
                        "Your current attempt will remain saved.",
                "Redo Quiz",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // ✅ CLEAN ARCHITECTURE: Use controller instead of direct access
            // This triggers: Controller → Interactor → Presenter → ViewModel
            // Then propertyChange() listener handles the navigation
            controller.prepareRedoQuiz(attemptId);
        }
    }

    /**
     * ✅ NEW METHOD: Handles navigation when ViewModel has a quiz to redo
     * This is triggered by the propertyChange listener when "quizToRedo" changes
     */
    private void handleRedoQuizNavigation(Quiz quiz) {
        List<Question> questions = quiz.getQuestions();
        
        if (questions == null || questions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "This quiz has no questions to redo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Navigate to QuizScreen
        frame.getContentPane().removeAll();
        frame.add(new QuizScreen(frame, questions, currentPlayer, 
                  completeQuizController, generateFromWrongController, 
                  generateFromWrongViewModel));
        frame.revalidate();
        frame.repaint();
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
        frame.add(new HomeScreen(frame, currentPlayer, generateFromWrongController,
                completeQuizController, generateFromWrongViewModel));
        frame.revalidate();
        frame.repaint();
    }
}