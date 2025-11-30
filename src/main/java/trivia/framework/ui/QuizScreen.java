package trivia.framework.ui;

import trivia.entity.Player;
import trivia.entity.Question;
import trivia.interface_adapter.controller.CompleteQuizController;
import trivia.interface_adapter.controller.GenerateFromWrongController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * QuizScreen — displays quiz questions and handles user answers
 * 
 * ✅ FULLY REFACTORED TO FOLLOW CLEAN ARCHITECTURE:
 * - NO direct DAO imports
 * - NO direct DAO instantiation
 * - Uses CompleteQuizController ONLY
 * - Pure UI logic - no business logic
 * - No data persistence - delegated to use case
 */
public class QuizScreen extends JPanel {
    private final JFrame frame;
    private final List<Question> questions;
    private final Player currentPlayer;
    private int currentIndex = 0;
    private final int numberOfQuestions;
    
    // Controllers
    private final CompleteQuizController completeQuizController;
    private final GenerateFromWrongController generateFromWrongController;
    
    // UI state
    private final List<String> userAnswers = new ArrayList<>();
    private final List<Integer> selectedAnswerIndices;

    // UI Components
    private final JTextArea questionArea;
    private final JRadioButton[] optionButtons;
    private final ButtonGroup group;
    private final JButton nextButton;
    private final JButton previousButton;
    private final JLabel progressLabel;

    public QuizScreen(JFrame frame,
                      List<Question> questions,
                      Player currentPlayer,
                      CompleteQuizController completeQuizController,
                      GenerateFromWrongController generateFromWrongController) {
        this.frame = frame;
        this.questions = questions;
        this.currentPlayer = currentPlayer;
        this.numberOfQuestions = questions.size();
        this.completeQuizController = completeQuizController;
        this.generateFromWrongController = generateFromWrongController;
        
        // Initialize tracking lists
        this.selectedAnswerIndices = new ArrayList<>();
        for (int i = 0; i < numberOfQuestions; i++) {
            selectedAnswerIndices.add(-1);
            userAnswers.add("");
        }

        setLayout(new BorderLayout(20, 20));
        ThemeUtils.applyGradientBackground(this);

        JLabel title = new JLabel("Quiz in Progress", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        
        progressLabel = new JLabel("Question 1 of " + numberOfQuestions, SwingConstants.CENTER);
        ThemeUtils.styleLabel(progressLabel, "body");
        
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.add(title);
        headerPanel.add(progressLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel quizPanel = ThemeUtils.createGlassPanel(60);
        quizPanel.setLayout(new BorderLayout(20, 20));
        quizPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        questionArea = new JTextArea();
        questionArea.setEditable(false);
        questionArea.setOpaque(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setFont(new Font("SansSerif", Font.BOLD, 20));
        questionArea.setFocusable(false);
        questionArea.setBorder(null);
        questionArea.setHighlighter(null);
        questionArea.setPreferredSize(new Dimension(700, 80));
        questionArea.setMaximumSize(new Dimension(700, 80));

        JPanel questionWrapper = new JPanel(new BorderLayout());
        questionWrapper.setOpaque(false);
        questionWrapper.add(questionArea, BorderLayout.CENTER);
        quizPanel.add(questionWrapper, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 12, 12));
        optionsPanel.setOpaque(false);
        group = new ButtonGroup();
        optionButtons = new JRadioButton[4];

        for (int i = 0; i < 4; i++) {
            JRadioButton button = new JRadioButton();
            button.setFont(ThemeUtils.BODY_FONT);
            button.setOpaque(false);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((JRadioButton) e.getSource()).setBackground(new Color(220, 250, 250, 150));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((JRadioButton) e.getSource()).setBackground(new Color(0, 0, 0, 0));
                }
            });

            group.add(button);
            optionButtons[i] = button;
            optionsPanel.add(button);
        }

        quizPanel.add(optionsPanel, BorderLayout.CENTER);
        add(quizPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        bottomPanel.setOpaque(false);

        previousButton = createStyledButton("Previous", 
            new Color(100, 100, 100), new Color(130, 130, 130), this::handlePrevious);
        nextButton = createStyledButton("Next", 
            ThemeUtils.MINT, ThemeUtils.MINT_HOVER, this::handleNext);
        
        previousButton.setEnabled(false);
        
        bottomPanel.add(previousButton);
        bottomPanel.add(nextButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadQuestion();
    }
    
    /**
     * Backward compatibility constructors
     */
    public QuizScreen(JFrame frame, List<Question> questions, Player currentPlayer,
                      CompleteQuizController completeQuizController) {
        this(frame, questions, currentPlayer, completeQuizController, null);
    }

    public QuizScreen(JFrame frame, List<Question> questions, Player currentPlayer) {
        this(frame, questions, currentPlayer, null, null);
    }

    private JButton createStyledButton(String text, Color base, Color hover, 
                                       java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(ThemeUtils.BUTTON_FONT);
        button.setBackground(base);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(hover); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(base); }
        });
        button.addActionListener(listener);
        return button;
    }

    private void loadQuestion() {
        if (currentIndex < questions.size()) {
            Question q = questions.get(currentIndex);
            questionArea.setText(q.getQuestionText());

            progressLabel.setText("Question " + (currentIndex + 1) + " of " + numberOfQuestions);

            List<String> opts = q.getOptions();
            for (int i = 0; i < optionButtons.length; i++) {
                if (i < opts.size()) {
                    optionButtons[i].setText(opts.get(i));
                    optionButtons[i].setVisible(true);
                } else {
                    optionButtons[i].setVisible(false);
                }
            }
            
            int previouslySelected = selectedAnswerIndices.get(currentIndex);
            group.clearSelection();
            if (previouslySelected >= 0 && previouslySelected < optionButtons.length) {
                optionButtons[previouslySelected].setSelected(true);
            }
            
            previousButton.setEnabled(currentIndex > 0);
            
            if (currentIndex == numberOfQuestions - 1) {
                nextButton.setText("Finish Quiz");
            } else {
                nextButton.setText("Next");
            }
        }
    }

    private void saveCurrentAnswer() {
        String chosen = null;
        int chosenIndex = -1;
        
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isVisible() && optionButtons[i].isSelected()) {
                chosen = optionButtons[i].getText();
                chosenIndex = i;
                break;
            }
        }
        
        if (chosen == null) {
            chosen = "";
            chosenIndex = -1;
        }
        
        if (currentIndex < userAnswers.size()) {
            userAnswers.set(currentIndex, chosen);
            selectedAnswerIndices.set(currentIndex, chosenIndex);
        }
    }

    private void handleNext(ActionEvent e) {
        saveCurrentAnswer();
        
        if (currentIndex == numberOfQuestions - 1) {
            finishQuiz();
        } else {
            currentIndex++;
            loadQuestion();
        }
    }
    
    private void handlePrevious(ActionEvent e) {
        saveCurrentAnswer();
        
        if (currentIndex > 0) {
            currentIndex--;
            loadQuestion();
        }
    }

    /**
     * ✅ CLEAN ARCHITECTURE: Delegate ALL persistence to controller
     * UI only computes final score for display
     */
    private void finishQuiz() {
        // Calculate score for display (UI logic)
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int correctIndex = q.getCorrectOptionIndex();
            int selectedIndex = selectedAnswerIndices.get(i);

            if (correctIndex >= 0 && correctIndex == selectedIndex) {
                score++;
            }
        }

        // ✅ CLEAN ARCHITECTURE: Call controller to handle ALL data persistence
        // The use case will:
        // - Create Quiz entity
        // - Create QuizAttempt entity
        // - Save to QuizDataAccessObject
        // - Save to PlayerDataAccessObject
        // - Update player score
        if (completeQuizController != null) {
            completeQuizController.execute(
                    currentPlayer.getPlayerName(),
                    questions,
                    userAnswers
            );
        }

        // Show completion message
        JOptionPane.showMessageDialog(frame,
                "Quiz complete!",
                "Summary",
                JOptionPane.INFORMATION_MESSAGE);

        // Navigate to summary screen
        frame.getContentPane().removeAll();
        frame.add(new SummaryScreen(score, numberOfQuestions, frame, currentPlayer,
                generateFromWrongController, completeQuizController));
        frame.revalidate();
        frame.repaint();
    }
}