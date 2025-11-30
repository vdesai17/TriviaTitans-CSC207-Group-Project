package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.interface_adapter.controller.CompleteQuizController;
import trivia.interface_adapter.presenter.GenerateFromWrongViewModel;
import trivia.interface_adapter.controller.ReviewSummaryController;
import trivia.interface_adapter.presenter.ReviewSummaryPresenter;
import trivia.interface_adapter.presenter.ReviewSummaryViewModel;
import trivia.interface_adapter.dao.QuizDataAccessObject;
import trivia.use_case.review_summary.ReviewSummaryInteractor;
import trivia.use_case.review_summary.ReviewSummaryResponseModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * SummaryScreen — displays the final quiz results (score & accuracy)
 * using the unified dark-teal gradient UI theme.
 * Allows returning to the HomeScreen while keeping the same player session.
 *
 * FIXED: Now properly stores and passes all controllers to HomeScreen
 */
public class SummaryScreen extends JPanel {
    private final JFrame frame;
    private final Player player;
    private final GenerateFromWrongController generateFromWrongController;
    private final CompleteQuizController completeQuizController;
    private final GenerateFromWrongViewModel generateFromWrongViewModel;

    public SummaryScreen(int score, int numberOfQuestions, JFrame frame, Player player,
                         GenerateFromWrongController generateFromWrongController,
                         CompleteQuizController completeQuizController,
                         GenerateFromWrongViewModel generateFromWrongViewModel) {
        this.frame = frame;
        this.player = player;
        this.generateFromWrongController = generateFromWrongController;
        this.completeQuizController = completeQuizController;
        this.generateFromWrongViewModel = generateFromWrongViewModel;

        ReviewSummaryViewModel viewModel = new ReviewSummaryViewModel();
        int accuracy = (numberOfQuestions != 0)
                ? Math.round(score * 100 / numberOfQuestions)
                : 0;

        ReviewSummaryResponseModel responseModel = new ReviewSummaryResponseModel(score, accuracy);
        ReviewSummaryPresenter presenter = new ReviewSummaryPresenter(viewModel);
        presenter.presentReviewSummary(responseModel);
        ReviewSummaryInteractor interactor = new ReviewSummaryInteractor(presenter);
        ReviewSummaryController controller = new ReviewSummaryController(interactor);
        controller.generateSummary(score, accuracy);

        setLayout(new BorderLayout());
        ThemeUtils.applyGradientBackground(this);

        JLabel title = new JLabel("Quiz Summary", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel contentPanel = ThemeUtils.createGlassPanel(40);
        contentPanel.setLayout(new GridLayout(3, 1, 15, 15));
        contentPanel.setOpaque(false);

        JLabel scoreLabel = new JLabel(viewModel.getScore(), SwingConstants.CENTER);
        ThemeUtils.styleLabel(scoreLabel, "subtitle");

        JLabel accuracyLabel = new JLabel(viewModel.getAccuracy(), SwingConstants.CENTER);
        ThemeUtils.styleLabel(accuracyLabel, "subtitle");

        JButton mainMenuButton = ThemeUtils.createStyledButton(
                "Return to Main Menu",
                ThemeUtils.MINT,
                ThemeUtils.MINT_HOVER
        );
        mainMenuButton.addActionListener(this::handleMainMenu);

        contentPanel.add(scoreLabel);
        contentPanel.add(accuracyLabel);
        contentPanel.add(mainMenuButton);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(contentPanel);
        add(centerWrapper, BorderLayout.CENTER);

        JLabel footer = new JLabel("Thank you for playing!", SwingConstants.CENTER);
        ThemeUtils.styleLabel(footer, "subtitle");
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        add(footer, BorderLayout.SOUTH);
    }

    public SummaryScreen(int score, int numberOfQuestions, JFrame frame, Player player) {
        this(score, numberOfQuestions, frame, player, null, null, null);
    }

    private void handleMainMenu(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(frame, player, generateFromWrongController,
                completeQuizController, new QuizDataAccessObject(), generateFromWrongViewModel));
        frame.revalidate();
        frame.repaint();
    }
}
