package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.GenerateFromWrongController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * HomeScreen - landing page after login/registration.
 * Provides navigation to Create Custom Quiz, API Quizzes, Load Existing Quiz, Profile/Stats, and Logout.
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
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Welcome, " + player.getPlayerName() + "!", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        // Buttons
        JButton createCustomQuizButton = new JButton("Create Custom Quiz");
        createCustomQuizButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        createCustomQuizButton.addActionListener(this::handleCreateCustomQuiz);

        JButton apiQuizButton = new JButton("API Quizzes");
        apiQuizButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        apiQuizButton.addActionListener(this::handleAPIQuiz);

        JButton loadQuizButton = new JButton("Load Existing Quiz");
        loadQuizButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        loadQuizButton.addActionListener(e ->
                JOptionPane.showMessageDialog(frame, "Feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE)
        );

        JButton profileButton = new JButton("View Profile / Statistics");
        profileButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        profileButton.addActionListener(this::handleViewProfile);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        logoutButton.setBackground(new Color(230, 57, 70));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(this::handleLogout);

        // Button Layout
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(80, 200, 50, 200));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(createCustomQuizButton);
        buttonPanel.add(apiQuizButton);
        buttonPanel.add(loadQuizButton);
        buttonPanel.add(profileButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(logoutButton);

        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Opens custom quiz creation screen
    private void handleCreateCustomQuiz(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new CreateCustomQuizScreen(frame, currentPlayer));
        frame.revalidate();
        frame.repaint();
    }

    // Opens the API quiz selection screen (unchanged existing logic)
    private void handleAPIQuiz(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new SelectQuizScreen(frame, generateFromWrongController, currentPlayer));
        frame.revalidate();
        frame.repaint();
    }

    // Opens profile/stats
    private void handleViewProfile(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new ProfileScreen(frame, currentPlayer, generateFromWrongController));
        frame.revalidate();
        frame.repaint();
    }

    // Logs out and returns to StartScreen
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
