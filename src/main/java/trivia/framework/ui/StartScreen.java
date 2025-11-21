package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.PlayerController;
import trivia.interface_adapter.dao.PlayerDataAccessObject;
import trivia.use_case.register_player.RegisterPlayerInteractor;

import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.interface_adapter.presenter.GenerateFromWrongPresenter;
import trivia.interface_adapter.presenter.GenerateFromWrongViewModel;
import trivia.use_case.generate_from_wrong.GenerateFromWrongDataAccessInterface;
import trivia.use_case.generate_from_wrong.GenerateFromWrongQuizInteractor;
import trivia.use_case.generate_from_wrong.GenerateFromWrongOutputBoundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StartScreen extends JPanel {
    private final JFrame frame;
    private final JTextField nameField;
    private final JPasswordField passwordField;
    private final PlayerController controller;
    private final PlayerDataAccessObject dao;
    private final GenerateFromWrongController generateFromWrongController;

    public StartScreen(JFrame frame) {
        this.frame = frame;

        // ---- Use Case 2 wiring (Player Registration) ----
        dao = new PlayerDataAccessObject();
        RegisterPlayerInteractor interactor = new RegisterPlayerInteractor(dao);
        this.controller = new PlayerController(interactor);

        GenerateFromWrongViewModel uc6ViewModel = new GenerateFromWrongViewModel();
        GenerateFromWrongOutputBoundary uc6Presenter = new GenerateFromWrongPresenter(uc6ViewModel);
        GenerateFromWrongDataAccessInterface uc6DataAccess = dao;
        GenerateFromWrongQuizInteractor uc6Interactor = new GenerateFromWrongQuizInteractor(uc6DataAccess, uc6Presenter);
        this.generateFromWrongController = new GenerateFromWrongController(uc6Interactor);

        // ---- UI Design ----
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Login or Register", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));

        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        loginButton.addActionListener(this::handleLogin);

        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        registerButton.addActionListener(this::handleRegister);

        JPanel center = new JPanel(new GridLayout(4, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(100, 150, 100, 150));
        center.add(new JLabel("Username:"));
        center.add(nameField);
        center.add(new JLabel("Password:"));
        center.add(passwordField);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 10, 10));
        bottom.add(loginButton);
        bottom.add(registerButton);

        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void handleLogin(ActionEvent e) {
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both name and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Player player = dao.validateLogin(name, password);
        if (player == null) {
            JOptionPane.showMessageDialog(frame, "Invalid credentials. Please try again or register.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Welcome back, " + player.getPlayerName() + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            navigateToHome(player);
        }
    }

    private void handleRegister(ActionEvent e) {
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both name and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Player newPlayer = new Player(name, password);
            dao.savePlayer(newPlayer);
            JOptionPane.showMessageDialog(frame, "Player registered successfully! Welcome, " + newPlayer.getPlayerName() + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            navigateToHome(newPlayer);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Failed to register player: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navigateToHome(Player player) {
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(frame, player, generateFromWrongController));
        frame.revalidate();
        frame.repaint();
    }
}
