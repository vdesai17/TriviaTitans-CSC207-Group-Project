package trivia.framework.ui;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Trivia Titans");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            // Initialize the StartScreen with login/register system
            StartScreen startScreen = new StartScreen(frame);
            frame.add(startScreen);

            frame.setVisible(true);
        });
    }
}