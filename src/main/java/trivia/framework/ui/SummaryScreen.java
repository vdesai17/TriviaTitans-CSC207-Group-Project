package trivia.framework.ui;

import trivia.entity.QuizAttempt;
import trivia.interface_adapter.controller.ReviewSummaryController;
import trivia.interface_adapter.presenter.ReviewSummaryPresenter;
import trivia.interface_adapter.presenter.ReviewSummaryViewModel;
import trivia.use_case.review_summary.ReviewSummaryInteractor;
import trivia.use_case.review_summary.ReviewSummaryResponseModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SummaryScreen extends JPanel {
    private final ReviewSummaryController controller;
    private final ReviewSummaryViewModel viewmodel;
    private final JFrame frame;

    private final JPanel panel;
    private final JButton startScreenButton = new JButton("Start Screen");
    private final JLabel quizTitle = new JLabel("", SwingConstants.LEFT);
    private final JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel accuracyLabel = new JLabel("", SwingConstants.RIGHT);

    public SummaryScreen(QuizAttempt quizAttempt, JFrame frame) {

        this.viewmodel = new ReviewSummaryViewModel();
        this.frame = frame;

        ReviewSummaryResponseModel responseModel = new ReviewSummaryResponseModel(quizAttempt.getScore, quizAttempt.getAccuracy);
        ReviewSummaryPresenter presenter = new ReviewSummaryPresenter(viewmodel);
        presenter.presentReviewSummary(responseModel);
        ReviewSummaryInteractor interactor = new ReviewSummaryInteractor(presenter);
        this.controller = new ReviewSummaryController(interactor);
        controller.generateSummary(quizAttempt);

        quizTitle.setText(viewmodel.getQuizTitle());
        quizTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        scoreLabel.setText(viewmodel.getScore());
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        accuracyLabel.setText(viewmodel.getAccuracy());
        accuracyLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        startScreenButton.setText("Main Menu");
        startScreenButton.setFont(new Font("SansSerif", Font.PLAIN, 16));

        startScreenButton.addActionListener(this::handleMainMenu);

        setLayout(new BorderLayout(15,15));
        setBackground(Color.WHITE);
        this.panel = new JPanel();

        panel.add(quizTitle);
        panel.add(scoreLabel);
        panel.add(accuracyLabel);
        panel.add(startScreenButton);

        controller.generateSummary(quizAttempt);
    }

    private void handleMainMenu(ActionEvent actionEvent) {
        StartScreen startScreen = new StartScreen(frame);
        frame.add(startScreen);

        frame.setVisible(true);


    }
}
