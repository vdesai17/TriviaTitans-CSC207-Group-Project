package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.CompleteQuizController;
import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.interface_adapter.controller.ReviewSummaryController;
import trivia.interface_adapter.presenter.ReviewSummaryViewModel;
import trivia.use_case.review_summary.ReviewSummaryRequestModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SummaryScreen extends JPanel {

    private final JFrame frame;
    private final Player player;
    private final ReviewSummaryViewModel viewModel;
    private final ReviewSummaryController reviewSummaryController;
    private final CompleteQuizController completeQuizController;
    private final GenerateFromWrongController generateFromWrongController;

    public SummaryScreen(int score,
                         int numberOfQuestions,
                         JFrame frame,
                         Player player,
                         ReviewSummaryViewModel viewModel,
                         ReviewSummaryController reviewSummaryController,
                         CompleteQuizController completeQuizController,
                         GenerateFromWrongController generateFromWrongController) {

        this.frame = frame;
        this.player = player;
        this.viewModel = viewModel;
        this.reviewSummaryController = reviewSummaryController;
        this.completeQuizController = completeQuizController;
        this.generateFromWrongController = generateFromWrongController;


        int accuracy = numberOfQuestions == 0 ? 0 : Math.round(score * 100 / numberOfQuestions);
        ReviewSummaryRequestModel request = new ReviewSummaryRequestModel(score, accuracy);

        reviewSummaryController.generateSummary(request);

        setLayout(new BorderLayout());
        ThemeUtils.applyGradientBackground(this);

        //Header
        JLabel title = new JLabel("Quiz Summary", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        //Content
        JPanel contentPanel = ThemeUtils.createGlassPanel(40);
        contentPanel.setLayout(new GridLayout(3, 1, 15, 15));
        contentPanel.setOpaque(false);

        JLabel scoreLabel = new JLabel(viewModel.getScore(), SwingConstants.CENTER);
        ThemeUtils.styleLabel(scoreLabel, "subtitle");

        JLabel accuracyLabel = new JLabel(viewModel.getAccuracy(), SwingConstants.CENTER);
        ThemeUtils.styleLabel(accuracyLabel, "subtitle");



        JButton mainMenuButton = createStyledButton(
                "Return to Main Menu",
                ThemeUtils.MINT,
                ThemeUtils.MINT_HOVER,
                this::handleMainMenu
        );

        contentPanel.add(scoreLabel);
        contentPanel.add(accuracyLabel);
        contentPanel.add(mainMenuButton);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(contentPanel);
        add(centerWrapper, BorderLayout.CENTER);

        //Footer
        JLabel footer = new JLabel("Thank you for playing!", SwingConstants.CENTER);
        ThemeUtils.styleLabel(footer, "subtitle");
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        add(footer, BorderLayout.SOUTH);
    }

    private void handleMainMenu(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(frame, player, generateFromWrongController, completeQuizController, null));
        frame.revalidate();
        frame.repaint();
    }
    //Make new style Button
    private JButton createStyledButton(String text, Color base, Color hover, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(ThemeUtils.BUTTON_FONT);
        button.setBackground(base);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(hover); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(base); }
        });
        button.addActionListener(listener);
        return button;
    }
}