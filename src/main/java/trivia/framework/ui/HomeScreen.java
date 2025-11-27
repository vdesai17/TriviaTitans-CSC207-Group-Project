package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.GenerateFromWrongController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * HomeScreen — central navigation hub after login.
 * Unified dark-teal gradient theme and modernized layout.
 */
public class HomeScreen extends JPanel {
    private final JFrame frame;
    private final Player currentPlayer;
    private final GenerateFromWrongController generateFromWrongController;

    public HomeScreen(JFrame frame, Player player, GenerateFromWrongController generateFromWrongController) {
        this.frame = frame;
        this.currentPlayer = player;
        this.generateFromWrongController = generateFromWrongController;

        setLayout(new BorderLayout(20, 20));
        ThemeUtils.applyGradientBackground(this);

        // --- Header ---
        JLabel title = new JLabel("Welcome, " + player.getPlayerName() + "!", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // --- Button Setup ---
        JButton createQuizButton = createStyledButton("Create Custom Quiz", ThemeUtils.MINT, ThemeUtils.MINT_HOVER, this::handleCreateCustomQuiz);
        JButton apiQuizButton = createStyledButton("API Quizzes", new Color(0, 180, 180), new Color(0, 200, 200), this::handleAPIQuiz);
        JButton loadQuizButton = createStyledButton("Load Existing Quiz", ThemeUtils.DEEP_TEAL, ThemeUtils.DEEP_TEAL_HOVER, this::handleLoadQuiz);
        JButton profileButton = createStyledButton("View Profile / Statistics", new Color(60, 180, 130), new Color(80, 220, 150), this::handleViewProfile);
        JButton logoutButton = createStyledButton("Logout", new Color(220, 80, 80), new Color(240, 100, 100), this::handleLogout);

        // --- Center Panel ---
        JPanel buttonPanel = ThemeUtils.createGlassPanel(60);
        buttonPanel.setLayout(new GridLayout(5, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(80, 250, 80, 250));

        buttonPanel.add(createQuizButton);
        buttonPanel.add(apiQuizButton);
        buttonPanel.add(loadQuizButton);
        buttonPanel.add(profileButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    /** Helper for consistent button design */
    private JButton createStyledButton(String text, Color base, Color hover, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(ThemeUtils.BUTTON_FONT);
        button.setBackground(base);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
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

    // --- Navigation Handlers ---
    private void handleCreateCustomQuiz(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new CreateCustomQuizScreen(frame, currentPlayer));
        frame.revalidate();
        frame.repaint();
    }

    private void handleAPIQuiz(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new SelectQuizScreen(frame, generateFromWrongController, currentPlayer));
        frame.revalidate();
        frame.repaint();
    }

    private void handleLoadQuiz(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new LoadQuizScreen(frame, currentPlayer));
        frame.revalidate();
        frame.repaint();
    }

    private void handleViewProfile(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new ProfileScreen(frame, currentPlayer, generateFromWrongController));
        frame.revalidate();
        frame.repaint();
    }

    private void handleLogout(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to log out?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            frame.getContentPane().removeAll();
            frame.add(new StartScreen(frame));
            frame.revalidate();
            frame.repaint();
        }
    }
}
