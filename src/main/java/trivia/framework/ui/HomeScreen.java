package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.interface_adapter.controller.CompleteQuizController;
import trivia.interface_adapter.controller.ReviewController;
import trivia.interface_adapter.controller.LoadQuizController;
import trivia.interface_adapter.dao.QuizDataAccessObject;
import trivia.interface_adapter.dao.PlayerDataAccessObject;
import trivia.interface_adapter.presenter.PastQuizPresenter;
import trivia.interface_adapter.presenter.PastQuizViewModel;
import trivia.interface_adapter.presenter.LoadQuizPresenter;
import trivia.interface_adapter.presenter.LoadQuizViewModel;
import trivia.use_case.load_quiz.LoadQuizInteractor;
import trivia.use_case.review_quiz.ReviewQuizInteractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * HomeScreen — central navigation hub after login.
 * Unified dark-teal gradient theme and modernized layout.
 */
public class HomeScreen extends JPanel {
    private final JFrame frame;
    private final Player currentPlayer;
    private final GenerateFromWrongController generateFromWrongController;
    private final CompleteQuizController completeQuizController;
    private final QuizDataAccessObject quizDAO;
    private final ReviewController reviewController;

    public HomeScreen(JFrame frame,
                      Player player,
                      GenerateFromWrongController generateFromWrongController,
                      CompleteQuizController completeQuizController,
                      QuizDataAccessObject quizDAO) {
        this.frame = frame;
        this.currentPlayer = player;
        this.generateFromWrongController = generateFromWrongController;
        this.completeQuizController = completeQuizController;
        this.quizDAO = quizDAO;

        // Initialize Review Quiz components
        PastQuizViewModel pastQuizViewModel = new PastQuizViewModel();
        PastQuizPresenter pastQuizPresenter = new PastQuizPresenter(pastQuizViewModel);
        PlayerDataAccessObject playerDAO = new PlayerDataAccessObject();
        ReviewQuizInteractor reviewQuizInteractor = new ReviewQuizInteractor(
                playerDAO, // implements ReviewQuizAttemptDataAccessInterface
                playerDAO, // implements ReviewQuizQuizDataAccessInterface
                pastQuizPresenter
        );
        this.reviewController = new ReviewController(reviewQuizInteractor);

        setLayout(new BorderLayout(20, 20));
        ThemeUtils.applyGradientBackground(this);

        JLabel title = new JLabel("Welcome, " + player.getPlayerName() + "!", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Buttons
        JButton createQuizButton = createStyledButton(
                "Create Custom Quiz", ThemeUtils.MINT, ThemeUtils.MINT_HOVER, this::handleCreateCustomQuiz);
        JButton apiQuizButton = createStyledButton(
                "API Quizzes", new Color(0, 180, 180), new Color(0, 200, 200), this::handleAPIQuiz);
        JButton loadQuizButton = createStyledButton(
                "Load Existing Quiz", ThemeUtils.DEEP_TEAL, ThemeUtils.DEEP_TEAL_HOVER, this::handleLoadQuiz);
        JButton reviewQuizButton = createStyledButton(
                "Review Past Quizzes", new Color(100, 100, 200), new Color(120, 120, 230), this::handleReviewQuizzes);
        JButton profileButton = createStyledButton(
                "View Profile / Statistics", new Color(60, 180, 130), new Color(80, 220, 150), this::handleViewProfile);
        JButton logoutButton = createStyledButton(
                "Logout", new Color(220, 80, 80), new Color(240, 100, 100), this::handleLogout);

        JPanel buttonPanel = ThemeUtils.createGlassPanel(60);
        buttonPanel.setLayout(new GridLayout(6, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(60, 250, 60, 250));
        buttonPanel.add(createQuizButton);
        buttonPanel.add(apiQuizButton);
        buttonPanel.add(loadQuizButton);
        buttonPanel.add(reviewQuizButton);
        buttonPanel.add(profileButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.CENTER);
    }

    public HomeScreen(JFrame frame,
                      Player player,
                      GenerateFromWrongController generateFromWrongController) {
        this(frame, player, generateFromWrongController, null, null);
    }

    private JButton createStyledButton(String text, Color base, Color hover, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(ThemeUtils.BUTTON_FONT);
        button.setBackground(base);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(hover); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(base); }
        });
        button.addActionListener(listener);
        return button;
    }

    private void handleCreateCustomQuiz(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new CreateCustomQuizScreen(frame, currentPlayer));
        frame.revalidate();
        frame.repaint();
    }

    private void handleAPIQuiz(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new SelectQuizScreen(frame, generateFromWrongController, completeQuizController, currentPlayer));
        frame.revalidate();
        frame.repaint();
    }

    private void handleLoadQuiz(ActionEvent e) {
    // ✅ Ensure DAO is reloaded every time user opens Load Existing Quiz
    QuizDataAccessObject dao = (quizDAO != null) ? quizDAO : new QuizDataAccessObject();

    // Reload data from file (fixes “Quiz data not initialized”)
    dao.getAllQuizzes(); // this call refreshes the internal static list

    LoadQuizPresenter loadQuizPresenter = new LoadQuizPresenter();
    LoadQuizInteractor loadQuizInteractor = new LoadQuizInteractor(dao, loadQuizPresenter);
    LoadQuizController loadQuizController = new LoadQuizController(loadQuizInteractor, loadQuizPresenter);
    LoadQuizViewModel loadQuizViewModel = new LoadQuizViewModel();

    frame.getContentPane().removeAll();
    frame.add(new LoadQuizScreen(
            frame,
            currentPlayer,
            loadQuizController,
            loadQuizViewModel,
            completeQuizController,
            generateFromWrongController,
            dao // ✅ pass refreshed DAO
    ));
    frame.revalidate();
    frame.repaint();
}

    private void handleReviewQuizzes(ActionEvent e) {
        PastQuizViewModel viewModel = new PastQuizViewModel();
        PastQuizPresenter presenter = new PastQuizPresenter(viewModel);
        PlayerDataAccessObject playerDAO = new PlayerDataAccessObject();
        ReviewQuizInteractor interactor = new ReviewQuizInteractor(playerDAO, playerDAO, presenter);
        ReviewController controller = new ReviewController(interactor);

        frame.getContentPane().removeAll();
        frame.add(new PastQuizScreen(frame, controller, viewModel, currentPlayer,
                generateFromWrongController, completeQuizController));
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
