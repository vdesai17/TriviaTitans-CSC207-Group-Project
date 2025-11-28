package trivia.framework.ui;

import trivia.entity.Player;
import trivia.entity.Question;
import trivia.entity.Quiz;
import trivia.entity.QuizAttempt;
import trivia.interface_adapter.controller.CompleteQuizController;
import trivia.interface_adapter.dao.QuizDataAccessObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuizScreen extends JPanel {
    private final JFrame frame;
    private final List<Question> questions;
    private final Player currentPlayer;
    private int currentIndex = 0;
    private int score = 0;
    private final int numberOfQuestions;
    private final CompleteQuizController controller;
    private final List<String> userAnswers = new ArrayList<>();

    private final JTextArea questionArea;
    private final JRadioButton[] optionButtons;
    private final ButtonGroup group;
    private final JButton nextButton;

    public QuizScreen(JFrame frame,
                      List<Question> questions,
                      Player currentPlayer,
                      CompleteQuizController controller) {
        this.frame = frame;
        this.questions = questions;
        this.currentPlayer = currentPlayer;
        this.numberOfQuestions = questions.size();
        this.controller = controller;

        setLayout(new BorderLayout(20, 20));
        ThemeUtils.applyGradientBackground(this);

        JLabel title = new JLabel("Quiz in Progress", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        add(title, BorderLayout.NORTH);

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

        nextButton = createStyledButton("Next", ThemeUtils.MINT, ThemeUtils.MINT_HOVER, this::handleNext);
        bottomPanel.add(nextButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadQuestion();
    }

    public QuizScreen(JFrame frame,
                      List<Question> questions,
                      Player currentPlayer) {
        this(frame, questions, currentPlayer, null);
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
            questionArea.setText(q.getQuestionText());

            List<String> opts = q.getOptions();
            for (int i = 0; i < optionButtons.length; i++) {
                if (i < opts.size()) {
                    optionButtons[i].setText(opts.get(i));
                    optionButtons[i].setVisible(true);
                } else {
                    optionButtons[i].setVisible(false);
                }
            }
            group.clearSelection();
        } else {
            endQuiz();
        }
    }

    private void handleNext(ActionEvent e) {
        Question q = questions.get(currentIndex);
        String chosen = null;
        for (JRadioButton btn : optionButtons) {
            if (btn.isVisible() && btn.isSelected()) {
                chosen = btn.getText();
                break;
            }
        }
        if (chosen == null) {
            chosen = "";
        }
        userAnswers.add(chosen);

        int correctIndex = q.getCorrectOptionIndex();
        if (correctIndex >= 0 && correctIndex < optionButtons.length && optionButtons[correctIndex].isSelected()) {
            score++;
        }

        currentIndex++;
        loadQuestion();
    }

    private void endQuiz() {
        QuizDataAccessObject quizDAO = new QuizDataAccessObject();

        String quizId = "api-" + System.currentTimeMillis();
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
                LocalDateTime.now(),
                userAnswers,
                score
        );

        quizDAO.saveQuiz(quiz);
        quizDAO.saveAttempt(attempt);

        if (controller != null) {
            controller.execute(
                    currentPlayer.getPlayerName(),
                    questions,
                    userAnswers
            );
        }

        JOptionPane.showMessageDialog(frame, "Quiz complete!", "Summary", JOptionPane.INFORMATION_MESSAGE);
        frame.getContentPane().removeAll();
        frame.add(new SummaryScreen(score, numberOfQuestions, frame, currentPlayer));
        frame.revalidate();
        frame.repaint();
    }
}
