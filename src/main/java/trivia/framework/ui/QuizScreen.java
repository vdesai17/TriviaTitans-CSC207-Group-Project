package trivia.framework.ui;

import trivia.entity.Player;
import trivia.entity.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * QuizScreen — handles displaying each question and user interaction during quizzes.
 * Styled with dark-teal gradient, glass-panel design, and consistent buttons.
 */
public class QuizScreen extends JPanel {
    private final JFrame frame;
    private final List<Question> questions;
    private final Player currentPlayer;
    private int currentIndex = 0;
    private int score = 0;
    private final int numberOfQuestions;

    private final JLabel questionLabel;
    private final JRadioButton[] optionButtons;
    private final ButtonGroup group;
    private final JButton nextButton;

    public QuizScreen(JFrame frame, List<Question> questions, Player currentPlayer) {
        this.frame = frame;
        this.questions = questions;
        this.currentPlayer = currentPlayer;
        this.numberOfQuestions = questions.size();

        setLayout(new BorderLayout(20, 20));
        ThemeUtils.applyGradientBackground(this);

        // --- Header ---
        JLabel title = new JLabel("Quiz in Progress", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        add(title, BorderLayout.NORTH);

        // --- Center (Glass Panel) ---
        JPanel quizPanel = ThemeUtils.createGlassPanel(60);
        quizPanel.setLayout(new BorderLayout(20, 20));
        quizPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Question
        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        questionLabel.setForeground(Color.BLACK);
        quizPanel.add(questionLabel, BorderLayout.NORTH);

        // Options
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 12, 12));
        optionsPanel.setOpaque(false);
        group = new ButtonGroup();
        optionButtons = new JRadioButton[4];

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(ThemeUtils.BODY_FONT);
            optionButtons[i].setOpaque(false);
            optionButtons[i].setFocusPainted(false);
            optionButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Add hover highlight
            optionButtons[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((JRadioButton) e.getSource()).setBackground(new Color(220, 250, 250, 150));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((JRadioButton) e.getSource()).setBackground(new Color(0, 0, 0, 0));
                }
            });

            group.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }
        quizPanel.add(optionsPanel, BorderLayout.CENTER);

        add(quizPanel, BorderLayout.CENTER);

        // --- Bottom Navigation ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        bottomPanel.setOpaque(false);

        nextButton = createStyledButton("Next", ThemeUtils.MINT, ThemeUtils.MINT_HOVER, this::handleNext);
        bottomPanel.add(nextButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadQuestion();
    }

    /** Creates a unified styled button with hover transitions. */
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

    /** Loads the current question and its options */
    private void loadQuestion() {
        if (currentIndex < questions.size()) {
            Question q = questions.get(currentIndex);
            questionLabel.setText("<html><div style='width:600px;text-align:center;'>" + q.getQuestionText() + "</div></html>");

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

    /** Handles user clicking “Next” and moves to the next question */
    private void handleNext(ActionEvent e) {
        Question q = questions.get(currentIndex);
        int correctIndex = q.getCorrectOptionIndex();

        if (correctIndex >= 0 && correctIndex < optionButtons.length && optionButtons[correctIndex].isSelected()) {
            score++;
        }

        currentIndex++;
        loadQuestion();
    }

    /** Ends the quiz and transitions to summary */
    private void endQuiz() {
        JOptionPane.showMessageDialog(frame, "Quiz complete!", "Summary", JOptionPane.INFORMATION_MESSAGE);
        frame.getContentPane().removeAll();
        frame.add(new SummaryScreen(score, numberOfQuestions, frame, currentPlayer));
        frame.revalidate();
        frame.repaint();
    }
}
