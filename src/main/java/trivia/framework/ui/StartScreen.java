package trivia.framework.ui;

import trivia.entity.Player;
import trivia.framework.AppFactory;
import trivia.interface_adapter.controller.PlayerController;
import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.interface_adapter.presenter.GenerateFromWrongViewModel;
import trivia.interface_adapter.controller.CompleteQuizController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * StartScreen — Login/Register entry page for Trivia Titans.
 * Styled with the unified dark-teal gradient theme.
 * 
 * CLEAN ARCHITECTURE: Uses AppFactory for all controller and DAO instantiation.
 */
public class StartScreen extends JPanel {
    private final JFrame frame;
    private final JTextField nameField;
    private final JPasswordField passwordField;
    private final PlayerController controller;
    private final GenerateFromWrongController generateFromWrongController;
    private final GenerateFromWrongViewModel generateFromWrongViewModel;
    private final CompleteQuizController completeQuizController;

    public StartScreen(JFrame frame) {
        this.frame = frame;

        // Use factory to create controllers
        this.controller = AppFactory.createPlayerController();
        this.generateFromWrongController = AppFactory.createGenerateFromWrongController();
        this.generateFromWrongViewModel = AppFactory.createGenerateFromWrongViewModel();
        this.completeQuizController = AppFactory.createCompleteQuizController();

        setLayout(new BorderLayout());
        ThemeUtils.applyGradientBackground(this);

        JLabel title = new JLabel("Trivia Titans", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");

        JLabel subtitle = new JLabel("Login or Register to Begin", SwingConstants.CENTER);
        ThemeUtils.styleLabel(subtitle, "subtitle");

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        headerPanel.add(title);
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = ThemeUtils.createGlassPanel(40);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel nameLabel = new JLabel("Username:");
        ThemeUtils.styleLabel(nameLabel, "body");
        formPanel.add(nameLabel, gbc);

        gbc.gridy++;
        nameField = new JTextField(15);
        nameField.setFont(ThemeUtils.BODY_FONT);
        nameField.setBackground(new Color(255, 255, 255, 200));
        nameField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        formPanel.add(nameField, gbc);

        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        ThemeUtils.styleLabel(passwordLabel, "body");
        formPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField(15);
        passwordField.setFont(ThemeUtils.BODY_FONT);
        passwordField.setBackground(new Color(255, 255, 255, 200));
        passwordField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 200, 60, 200));

        JButton loginButton = createStyledButton("Login",
                ThemeUtils.DEEP_TEAL, ThemeUtils.DEEP_TEAL_HOVER);
        loginButton.addActionListener(this::handleLogin);

        JButton registerButton = createStyledButton("Register",
                ThemeUtils.MINT, ThemeUtils.MINT_HOVER);
        registerButton.addActionListener(this::handleRegister);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color base, Color hover) {
        JButton button = new JButton(text);
        button.setFont(ThemeUtils.BUTTON_FONT);
        button.setBackground(base);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(base);
            }
        });

        return button;
    }

    private void handleLogin(ActionEvent e) {
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter both name and password.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Player player = AppFactory.getPlayerDAO().validateLogin(name, password);
        if (player == null) {
            JOptionPane.showMessageDialog(frame,
                    "Invalid credentials. Please try again or register.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Welcome back, " + player.getPlayerName() + "!",
                    "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            navigateToHome(player);
        }
    }

    private void handleRegister(ActionEvent e) {
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter both name and password.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Player newPlayer = new Player(name, password);
            AppFactory.getPlayerDAO().savePlayer(newPlayer);
            JOptionPane.showMessageDialog(frame,
                    "Player registered successfully! Welcome, "
                            + newPlayer.getPlayerName() + ".",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            navigateToHome(newPlayer);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Failed to register player: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navigateToHome(Player player) {
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(
                frame, 
                player,
                generateFromWrongController,
                completeQuizController,
                generateFromWrongViewModel
        ));
        frame.revalidate();
        frame.repaint();
    }
}