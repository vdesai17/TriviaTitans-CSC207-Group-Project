package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.ReviewSummaryController;
import trivia.interface_adapter.presenter.ReviewSummaryPresenter;
import trivia.interface_adapter.presenter.ReviewSummaryViewModel;
import trivia.use_case.review_summary.ReviewSummaryInteractor;
import trivia.use_case.review_summary.ReviewSummaryResponseModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * SummaryScreen — displays the final quiz results (score & accuracy)
 * and allows returning to the HomeScreen while keeping the same player session.
 */
public class SummaryScreen extends JPanel {
    private final JFrame frame;
    private final Player player;

    public SummaryScreen(int score, int numberOfQuestions, JFrame frame, Player player) {
        this.frame = frame;
        this.player = player;

        ReviewSummaryViewModel viewModel = new ReviewSummaryViewModel();

        int accuracy = 0;
        if (numberOfQuestions != 0) {
            accuracy = Math.round(score * 100 / numberOfQuestions);
        }

        // Set up Clean Architecture components
        ReviewSummaryResponseModel responseModel = new ReviewSummaryResponseModel(score, accuracy);
        ReviewSummaryPresenter presenter = new ReviewSummaryPresenter(viewModel);
        presenter.presentReviewSummary(responseModel);
        ReviewSummaryInteractor interactor = new ReviewSummaryInteractor(presenter);
        ReviewSummaryController controller = new ReviewSummaryController(interactor);
        controller.generateSummary(score, accuracy);

        // UI Components
        JLabel scoreLabel = new JLabel(viewModel.getScore(), SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel accuracyLabel = new JLabel(viewModel.getAccuracy(), SwingConstants.CENTER);
        accuracyLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        mainMenuButton.addActionListener(this::handleMainMenu);

        // Layout
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(100, 150, 100, 150));
        infoPanel.add(scoreLabel);
        infoPanel.add(accuracyLabel);
        infoPanel.add(mainMenuButton);

        add(infoPanel, BorderLayout.CENTER);
    }

    private void handleMainMenu(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(frame, player, null)); // back to logged-in player's home screen
        frame.revalidate();
        frame.repaint();
    }
}
