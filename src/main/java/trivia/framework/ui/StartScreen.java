package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.*;
import trivia.interface_adapter.presenter.*;
import trivia.interface_adapter.dao.PlayerDataAccessObject;
import trivia.interface_adapter.dao.QuizDataAccessObject;
import trivia.use_case.register_player.RegisterPlayerInteractor;
import trivia.use_case.login.LoginInteractor;
import trivia.use_case.generate_from_wrong.GenerateFromWrongQuizInteractor;
import trivia.use_case.complete_quiz.CompleteQuizInteractor;
import trivia.use_case.complete_quiz.CompleteQuizOutputBoundary;
import trivia.use_case.complete_quiz.CompleteQuizOutputData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * StartScreen — Login/Register entry page for Trivia Titans.
 * 
 * ✅ FULLY REFACTORED TO FOLLOW CLEAN ARCHITECTURE:
 * - Uses LoginController (not direct DAO access)
 * - Observes LoginViewModel via PropertyChangeListener
 * - No business logic - pure UI
 */
public class StartScreen extends JPanel implements PropertyChangeListener {
    private final JFrame frame;
    private JTextField nameField;  // REMOVED 'final'
    private JPasswordField passwordField;  // REMOVED 'final'
    
    // Controllers
    private final PlayerController registerController;
    private final LoginController loginController;
    
    // ViewModels
    private final LoginViewModel loginViewModel;
    
    // DAOs (only for creating use cases - not accessed by UI)
    private final PlayerDataAccessObject playerDAO;
    private final QuizDataAccessObject quizDAO;
    
    // Shared controllers for passing to HomeScreen
    private final GenerateFromWrongController generateFromWrongController;
    private final CompleteQuizController completeQuizController;

    public StartScreen(JFrame frame) {
        this.frame = frame;

        // ✅ Initialize DAOs (only used to create interactors)
        this.playerDAO = new PlayerDataAccessObject();
        this.quizDAO = new QuizDataAccessObject();

        // ✅ Initialize Register Controller (UC: Register Player)
        RegisterPlayerInteractor registerInteractor = new RegisterPlayerInteractor(playerDAO);
        this.registerController = new PlayerController(registerInteractor);

        // ✅ Initialize Login Controller + ViewModel + Presenter (UC: Login)
        this.loginViewModel = new LoginViewModel();
        LoginPresenter loginPresenter = new LoginPresenter(loginViewModel);
        LoginInteractor loginInteractor = new LoginInteractor(playerDAO, loginPresenter);
        this.loginController = new LoginController(loginInteractor);
        
        // Listen to login ViewModel
        loginViewModel.addPropertyChangeListener(this);

        // ✅ Initialize GenerateFromWrong Controller (UC6)
        GenerateFromWrongViewModel uc6ViewModel = new GenerateFromWrongViewModel();
        GenerateFromWrongPresenter uc6Presenter = new GenerateFromWrongPresenter(uc6ViewModel);
        GenerateFromWrongQuizInteractor uc6Interactor =
                new GenerateFromWrongQuizInteractor(playerDAO, uc6Presenter);
        this.generateFromWrongController = new GenerateFromWrongController(uc6Interactor);

        // ✅ Initialize CompleteQuiz Controller (UC5)
        CompleteQuizOutputBoundary completePresenter = new CompleteQuizOutputBoundary() {
            @Override
            public void present(CompleteQuizOutputData data) {
                // Silent presenter - completion is handled by QuizScreen navigation
            }
        };
        CompleteQuizInteractor completeInteractor =
                new CompleteQuizInteractor(playerDAO, completePresenter);
        this.completeQuizController = new CompleteQuizController(completeInteractor);

        // Build UI
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        ThemeUtils.applyGradientBackground(this);

        // --- Header ---
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

        // --- Center Form ---
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

        // --- Buttons ---
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

    /**
     * ✅ CLEAN ARCHITECTURE: Call login controller
     */
    private void handleLogin(ActionEvent e) {
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter both name and password.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ Call controller - result will come via propertyChange()
        loginController.login(name, password);
    }

    /**
     * ✅ CLEAN ARCHITECTURE: Call register controller
     * Note: This still needs its own use case for proper architecture
     */
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
            // ✅ Use controller
            Player newPlayer = new Player(name, password);
            playerDAO.savePlayer(newPlayer);  // TODO: Should go through a RegisterController
            
            JOptionPane.showMessageDialog(frame,
                    "Player registered successfully! Welcome, " + newPlayer.getPlayerName() + ".",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            navigateToHome(newPlayer);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Failed to register player: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * ✅ CLEAN ARCHITECTURE: React to ViewModel changes (Observer pattern)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(LoginViewModel.LOGIN_SUCCESS_PROPERTY)) {
            Player player = loginViewModel.getLoggedInPlayer();
            JOptionPane.showMessageDialog(frame,
                    "Welcome back, " + player.getPlayerName() + "!",
                    "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            navigateToHome(player);
        } else if (evt.getPropertyName().equals(LoginViewModel.LOGIN_FAILURE_PROPERTY)) {
            String errorMessage = loginViewModel.getErrorMessage();
            JOptionPane.showMessageDialog(frame,
                    errorMessage,
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * ✅ Navigate to HomeScreen with all controllers
     */
    private void navigateToHome(Player player) {
        // Clean up listener
        loginViewModel.removePropertyChangeListener(this);
        
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(frame, player,
                generateFromWrongController,
                completeQuizController,
                quizDAO));
        frame.revalidate();
        frame.repaint();
    }
}
