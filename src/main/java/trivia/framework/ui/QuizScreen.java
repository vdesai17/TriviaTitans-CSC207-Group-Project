package trivia.framework.ui;

import trivia.entity.Player;
import trivia.entity.Question;
import trivia.entity.Quiz;
import trivia.entity.QuizAttempt;
import trivia.framework.AppFactory;
import trivia.interface_adapter.controller.CompleteQuizController;
import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.interface_adapter.controller.ReviewSummaryController;
import trivia.interface_adapter.presenter.GenerateFromWrongViewModel;
import trivia.interface_adapter.presenter.ReviewSummaryViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * QuizScreen — displays quiz questions and collects user answers.
 * 
 * CLEAN ARCHITECTURE: Dependencies injected through constructor.
 * Uses AppFactory for DAO access during completion.
 * 
 * FIXED: HTML entity decoding for questions and options from API
 */
public class QuizScreen extends JPanel {
    private final JFrame frame;
    private final List<Question> questions;
    private final Player currentPlayer;
    private int currentIndex = 0;
    private int score = 0;
    private final int numberOfQuestions;
    private final CompleteQuizController controller;
    private final GenerateFromWrongController generateFromWrongController;
    private final GenerateFromWrongViewModel generateFromWrongViewModel;
    private final List<String> userAnswers = new ArrayList<>();

    // Track selected answer index for each question (-1 means no selection)
    private final List<Integer> selectedAnswerIndices;

    private final JTextArea questionArea;
    private final JRadioButton[] optionButtons;
    private final ButtonGroup group;
    private final JButton nextButton;
    private final JButton previousButton;
    private final JLabel progressLabel;


    public QuizScreen(JFrame frame,
                      List<Question> questions,
                      Player currentPlayer,
                      CompleteQuizController controller,
                      GenerateFromWrongController generateFromWrongController,
                      GenerateFromWrongViewModel generateFromWrongViewModel) {
        this.frame = frame;
        this.questions = questions;
        this.currentPlayer = currentPlayer;
        this.numberOfQuestions = questions.size();
        this.controller = controller;
        this.generateFromWrongController = generateFromWrongController;
        this.generateFromWrongViewModel = generateFromWrongViewModel;

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

        previousButton = createStyledButton("Previous", new Color(100, 100, 100), new Color(130, 130, 130), this::handlePrevious);
        nextButton = createStyledButton("Next", ThemeUtils.MINT, ThemeUtils.MINT_HOVER, this::handleNext);

        previousButton.setEnabled(false);

        bottomPanel.add(previousButton);
        bottomPanel.add(nextButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadQuestion();
    }

    /**
     * Constructor for backward compatibility
     */
    public QuizScreen(JFrame frame,
                      List<Question> questions,
                      Player currentPlayer,
                      CompleteQuizController controller,
                      GenerateFromWrongController generateFromWrongController) {
        this(frame, questions, currentPlayer, controller, generateFromWrongController, null);
    }

    public QuizScreen(JFrame frame,
                      List<Question> questions,
                      Player currentPlayer,
                      CompleteQuizController controller) {
        this(frame, questions, currentPlayer, controller, null, null);
    }

    public QuizScreen(JFrame frame,
                      List<Question> questions,
                      Player currentPlayer) {
        this(frame, questions, currentPlayer, null, null, null);
    }

    /**
     * ✅ FIXED: Decode HTML entities from API responses
     * Converts &quot; → ", &amp; → &, &lt; → <, etc.
     */
    private String decodeHtmlEntities(String text) {
        if (text == null) return null;
        return text.replace("&quot;", "\"")
                   .replace("&#039;", "'")
                   .replace("&apos;", "'")
                   .replace("&amp;", "&")
                   .replace("&lt;", "<")
                   .replace("&gt;", ">")
                   .replace("&deg;", "°")
                   .replace("&ndash;", "–")
                   .replace("&mdash;", "—");
    }

    private JButton createStyledButton(String text, Color base, Color hover, java.awt.event.ActionListener listener) {
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
            
            // ✅ FIXED: Decode HTML entities in question text
            questionArea.setText(decodeHtmlEntities(q.getQuestionText()));

            progressLabel.setText("Question " + (currentIndex + 1) + " of " + numberOfQuestions);

            List<String> opts = q.getOptions();
            for (int i = 0; i < optionButtons.length; i++) {
                if (i < opts.size()) {
                    // ✅ FIXED: Decode HTML entities in option text
                    optionButtons[i].setText(decodeHtmlEntities(opts.get(i)));
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

    private void finishQuiz() {
        score = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int correctIndex = q.getCorrectOptionIndex();
            int selectedIndex = selectedAnswerIndices.get(i);

            if (correctIndex >= 0 && correctIndex == selectedIndex) {
                score++;
            }
        }

        String quizId = "quiz-" + System.currentTimeMillis();
        Quiz quiz = new Quiz(
                quizId,
                "API Quiz",
                "general",
                "mixed",
                currentPlayer.getPlayerName(),
                questions
        );

        QuizAttempt attempt = new QuizAttempt(
                "attempt-" + System.currentTimeMillis(),
                quiz,
                questions.size(),
                currentPlayer.getPlayerName(),
                java.time.LocalDateTime.now().toString(),
                userAnswers,
                score
        );

        attempt.setSelectedOptionIndices(new ArrayList<>(selectedAnswerIndices));

        AppFactory.getQuizDAO().saveQuiz(quiz);
        AppFactory.getQuizDAO().saveAttempt(attempt);

        Player player = AppFactory.getPlayerDAO().loadPlayer(currentPlayer.getPlayerName());
        if (player != null) {
            player.addAttempt(attempt);
            player.setScore(player.getScore() + score);
            AppFactory.getPlayerDAO().savePlayer(player);
            System.out.println("✓ Quiz attempt saved to player: " + player.getPlayerName());
        } else {
            System.err.println("⚠ Warning: Could not load player to save attempt");
        }

        if (controller != null) {
            controller.execute(
                    currentPlayer.getPlayerName(),
                    questions,
                    userAnswers
            );
        }

        JOptionPane.showMessageDialog(
                frame,
                "Quiz complete!",
                "Summary",
                JOptionPane.INFORMATION_MESSAGE
        );

        ReviewSummaryViewModel reviewViewModel = AppFactory.createReviewSummaryViewModel();
        ReviewSummaryController reviewController = AppFactory.createReviewSummaryController();

        frame.getContentPane().removeAll();
        frame.add(new SummaryScreen(score, numberOfQuestions, frame, currentPlayer, reviewViewModel,
                reviewController, controller, generateFromWrongController));
        frame.revalidate();
        frame.repaint();
    }
}