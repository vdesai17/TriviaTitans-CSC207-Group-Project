package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.GenerateFromWrongController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ProfileScreen — displays player statistics inside a glass panel
 * with unified dark-teal gradient styling and consistent navigation buttons.
 */
public class ProfileScreen extends JPanel {
    private final JFrame frame;
    private final Player player;
    private final GenerateFromWrongController generateFromWrongController;

    public ProfileScreen(JFrame frame, Player player, GenerateFromWrongController generateFromWrongController) {
        this.frame = frame;
        this.player = player;
        this.generateFromWrongController = generateFromWrongController;

        setLayout(new BorderLayout(20, 20));
        ThemeUtils.applyGradientBackground(this);

        // --- Header ---
        JLabel title = new JLabel("Player Profile", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        add(title, BorderLayout.NORTH);

        // --- Center Glass Panel ---
        JPanel profilePanel = ThemeUtils.createGlassPanel(60);
        profilePanel.setLayout(new GridLayout(4, 1, 15, 15));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(60, 150, 60, 150));

        JLabel nameLabel = new JLabel("Username: " + player.getPlayerName());
        ThemeUtils.styleLabel(nameLabel, "body");

        JLabel scoreLabel = new JLabel("Total Score: " + player.getScore());
        ThemeUtils.styleLabel(scoreLabel, "body");

        JLabel attemptsLabel = new JLabel("Total Quizzes Attempted: " + player.getPastAttempts().size());
        ThemeUtils.styleLabel(attemptsLabel, "body");

        profilePanel.add(nameLabel);
        profilePanel.add(scoreLabel);
        profilePanel.add(attemptsLabel);
        add(profilePanel, BorderLayout.CENTER);

        // --- Bottom Buttons ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        bottomPanel.setOpaque(false);

        JButton backButton = createStyledButton("Back to Home", ThemeUtils.MINT, ThemeUtils.MINT_HOVER, this::goBackHome);
        JButton logoutButton = createStyledButton("Logout", new Color(230, 57, 70), new Color(255, 90, 90), this::handleLogout);

        bottomPanel.add(backButton);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /** Creates a styled button with hover transitions */
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

    private void goBackHome(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(frame, player, generateFromWrongController));
        frame.revalidate();
        frame.repaint();
    }

    private void handleLogout(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to log out?",
                "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            frame.getContentPane().removeAll();
            frame.add(new StartScreen(frame));
            frame.revalidate();
            frame.repaint();
        }
    }
}
