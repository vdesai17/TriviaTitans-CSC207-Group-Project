package trivia.framework.ui;

import trivia.interface_adapter.controller.ReviewSummaryController;
import trivia.interface_adapter.presenter.ReviewSummaryPresenter;
import trivia.interface_adapter.presenter.ReviewSummaryViewModel;
import trivia.use_case.review_summary.ReviewSummaryInteractor;
import trivia.use_case.review_summary.ReviewSummaryResponseModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SummaryScreen extends JPanel {
    private final JFrame frame;

    public SummaryScreen(int score, int numberOfQuestions, JFrame frame) {

        ReviewSummaryViewModel viewModel = new ReviewSummaryViewModel();
        this.frame = frame;
        int accuracy = 0;
        if (numberOfQuestions != 0) {
            accuracy =  Math.round(score * 100 / numberOfQuestions);
        }

        ReviewSummaryResponseModel responseModel = new ReviewSummaryResponseModel(score, accuracy);
        ReviewSummaryPresenter presenter = new ReviewSummaryPresenter(viewModel);
        presenter.presentReviewSummary(responseModel);
        ReviewSummaryInteractor interactor = new ReviewSummaryInteractor(presenter);
        ReviewSummaryController controller = new ReviewSummaryController(interactor);
        controller.generateSummary(score, accuracy);


        JLabel scoreLabel = new JLabel("", SwingConstants.LEFT);
        scoreLabel.setText(viewModel.getScore());
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JLabel accuracyLabel = new JLabel("", SwingConstants.CENTER);
        accuracyLabel.setText(viewModel.getAccuracy());
        accuracyLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JButton startScreenButton = new JButton("Start Screen");
        startScreenButton.setText("Main Menu");
        startScreenButton.setFont(new Font("SansSerif", Font.PLAIN, 16));

        startScreenButton.addActionListener(this::handleMainMenu);

        setLayout(new BorderLayout(15,15));
        setBackground(Color.WHITE);
        JPanel panel = new JPanel();
        panel.add(scoreLabel);
        panel.add(accuracyLabel);
        panel.add(startScreenButton);

        add(panel, BorderLayout.CENTER);
    }

    private void handleMainMenu(ActionEvent actionEvent) {
        StartScreen startScreen = new StartScreen(frame);
        frame.getContentPane().removeAll();
        frame.add(startScreen);
        frame.revalidate();
        frame.repaint();


    }
}
