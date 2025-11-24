package trivia.framework.ui;

import trivia.entity.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class QuizScreen extends JPanel {
    private final JFrame frame;
    private final List<Question> questions;
    private int currentIndex = 0;
    private int score = 0;
    private final int numberofquestions;

    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup group;
    private JButton nextButton;

    public QuizScreen(JFrame frame, List<Question> questions) {
        this.frame = frame;
        this.questions = questions;
        this.numberofquestions = questions.size();
        setLayout(new BorderLayout(15,15));
        setBackground(Color.WHITE);

        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel optionsPanel = new JPanel(new GridLayout(4,1,5,5));
        group = new ButtonGroup();
        optionButtons = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
            group.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }

        nextButton = new JButton("Next");
        nextButton.addActionListener(this::handleNext);

        add(questionLabel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.CENTER);
        add(nextButton, BorderLayout.SOUTH);

        loadQuestion();
    }

    private void loadQuestion() {
        if (currentIndex < questions.size()) {
            Question q = questions.get(currentIndex);
            questionLabel.setText("<html>" + q.getQuestionText() + "</html>");

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
            JOptionPane.showMessageDialog(frame, "Quiz finished!");
            // TODO: move to summary screen later
            SummaryScreen summaryScreen = new SummaryScreen(score, numberofquestions, frame);
            frame.getContentPane().removeAll();
            frame.add(summaryScreen);
            frame.revalidate();
            frame.repaint();
        }
    }

    private void handleNext(ActionEvent e) {
        Question q = questions.get(currentIndex);
        if (optionButtons[q.getCorrectOptionIndex()].isSelected()) {
            score++;
        }
        currentIndex++;
        loadQuestion();
    }
}
