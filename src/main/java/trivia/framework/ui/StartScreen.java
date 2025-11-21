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
    private final PlayerController controller;

    private final GenerateFromWrongController generateFromWrongController;

    public StartScreen(JFrame frame) {
        this.frame = frame;

        // ---- Use Case 2 wiring (Player Registration) ----
        PlayerDataAccessObject dao = new PlayerDataAccessObject();
        RegisterPlayerInteractor interactor = new RegisterPlayerInteractor(dao);
        this.controller = new PlayerController(interactor);

        GenerateFromWrongViewModel uc6ViewModel = new GenerateFromWrongViewModel();
        GenerateFromWrongOutputBoundary uc6Presenter = new GenerateFromWrongPresenter(uc6ViewModel);

        GenerateFromWrongDataAccessInterface uc6DataAccess = dao;

        GenerateFromWrongQuizInteractor uc6Interactor =
                new GenerateFromWrongQuizInteractor(uc6DataAccess, uc6Presenter);

        this.generateFromWrongController = new GenerateFromWrongController(uc6Interactor);

        // ---- UI Design ----
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Enter Your Name", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));

        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JButton startButton = new JButton("Start Quiz");
        startButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        startButton.addActionListener(this::handleStart);

        JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(100, 150, 100, 150));
        center.add(nameField);
        center.add(startButton);

        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private void handleStart(ActionEvent e) {
        String name = nameField.getText();
        try {
            Player player = controller.createPlayer(name);

            JOptionPane.showMessageDialog(frame,
                    "Welcome, " + player.getPlayerName() + "!",
                    "Player Registered",
                    JOptionPane.INFORMATION_MESSAGE);

            frame.getContentPane().removeAll();
            frame.add(new SelectQuizScreen(frame, generateFromWrongController));
            frame.revalidate();
            frame.repaint();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(frame,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
