package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.GenerateFromWrongController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * ProfileScreen - shows player name, total score, and number of attempts.
 * Includes Back button to return to HomeScreen.
 */
public class ProfileScreen extends JPanel {
    private final JFrame frame;
    private final Player player;
    private final GenerateFromWrongController generateFromWrongController;

    public ProfileScreen(JFrame frame, Player player, GenerateFromWrongController generateFromWrongController) {
        this.frame = frame;
        this.player = player;
        this.generateFromWrongController = generateFromWrongController;

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Player Profile", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(80, 200, 80, 200));
        statsPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("Name: " + player.getPlayerName());
        JLabel scoreLabel = new JLabel("Total Score: " + player.getScore());
        JLabel attemptsLabel = new JLabel("Total Quizzes Attempted: " + player.getPastAttempts().size());

        statsPanel.add(nameLabel);
        statsPanel.add(scoreLabel);
        statsPanel.add(attemptsLabel);

        JButton backButton = new JButton("Back to Home");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        backButton.addActionListener(this::goBack);

        add(statsPanel, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }

    private void goBack(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(frame, player, generateFromWrongController));
        frame.revalidate();
        frame.repaint();
    }
}
