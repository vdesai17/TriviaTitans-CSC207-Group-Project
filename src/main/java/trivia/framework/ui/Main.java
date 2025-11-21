package trivia.framework.ui;

import javax.swing.*;

/**
 * Application entry point for Trivia Titans.
 * Starts with the Login/Register StartScreen.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Trivia Titans");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            // Start with login/register screen
            frame.setContentPane(new StartScreen(frame));
            frame.setVisible(true);
        });
    }
}
