package trivia.framework.ui;

import trivia.entity.Player;
import trivia.interface_adapter.controller.GenerateFromWrongController;
import trivia.interface_adapter.controller.CompleteQuizController;
import trivia.interface_adapter.dao.PlayerDataAccessObject;
import trivia.interface_adapter.dao.QuizDataAccessObject;
import trivia.interface_adapter.presenter.GenerateFromWrongViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

/**
 * ProfileScreen — displays player statistics and shows
 * how this player performs compared to other players.
 */
public class ProfileScreen extends JPanel {
    private final JFrame frame;
    private final GenerateFromWrongController generateFromWrongController;
    private final CompleteQuizController completeQuizController;
    private final GenerateFromWrongViewModel generateFromWrongViewModel;
    private final PlayerDataAccessObject playerDAO;

    private Player player;

    public ProfileScreen(JFrame frame, Player player,
                         GenerateFromWrongController generateFromWrongController,
                         CompleteQuizController completeQuizController,
                         GenerateFromWrongViewModel generateFromWrongViewModel) {
        this.frame = frame;
        this.generateFromWrongController = generateFromWrongController;
        this.completeQuizController = completeQuizController;
        this.generateFromWrongViewModel = generateFromWrongViewModel;
        this.playerDAO = new PlayerDataAccessObject();

        Player reloaded = playerDAO.loadPlayer(player.getPlayerName());
        if (reloaded != null) {
            this.player = reloaded;
        } else {
            this.player = player;
        }

        setLayout(new BorderLayout(20, 20));
        ThemeUtils.applyGradientBackground(this);

        JLabel title = new JLabel("Player Profile", SwingConstants.CENTER);
        ThemeUtils.styleLabel(title, "title");
        add(title, BorderLayout.NORTH);

        JPanel profilePanel = ThemeUtils.createGlassPanel(60);
        profilePanel.setLayout(new GridLayout(4, 1, 15, 15));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(60, 150, 60, 150));

        JLabel nameLabel = new JLabel("Username: " + this.player.getPlayerName());
        ThemeUtils.styleLabel(nameLabel, "body");

        JLabel scoreLabel = new JLabel("Total Score: " + this.player.getScore());
        ThemeUtils.styleLabel(scoreLabel, "body");

        int attemptsCount = (this.player.getPastAttempts() == null)
                ? 0
                : this.player.getPastAttempts().size();
        JLabel attemptsLabel = new JLabel("Total Quizzes Attempted: " + attemptsCount);
        ThemeUtils.styleLabel(attemptsLabel, "body");

        JLabel rankingLabel = new JLabel(getRankingText());
        ThemeUtils.styleLabel(rankingLabel, "body");
        rankingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        profilePanel.add(nameLabel);
        profilePanel.add(scoreLabel);
        profilePanel.add(attemptsLabel);
        profilePanel.add(rankingLabel);
        add(profilePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        bottomPanel.setOpaque(false);

        JButton backButton = createStyledButton(
                "Back to Home",
                ThemeUtils.MINT,
                ThemeUtils.MINT_HOVER,
                this::goBackHome
        );
        JButton logoutButton = createStyledButton(
                "Logout",
                new Color(230, 57, 70),
                new Color(255, 90, 90),
                this::handleLogout
        );

        bottomPanel.add(backButton);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private String getRankingText() {
        List<Player> allPlayers = playerDAO.getAllPlayers();
        if (allPlayers == null || allPlayers.isEmpty()) {
            return "No players have played any quizzes yet.";
        }

        allPlayers.sort(Comparator.comparingInt(Player::getScore).reversed());

        int totalPlayers = allPlayers.size();
        int myScore = player.getScore();
        int rank = -1;
        int position = 1;

        for (Player p : allPlayers) {
            if (p.getPlayerName().equalsIgnoreCase(player.getPlayerName())) {
                rank = position;
                break;
            }
            position++;
        }

        if (rank == -1) {
            return "Your total score: " + myScore + " | You are not in the ranking yet.";
        } else {
            return "Your total score: " + myScore +
                    " | Rank: " + rank + " out of " + totalPlayers + " players.";
        }
    }

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

    private void goBackHome(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new HomeScreen(frame, player, generateFromWrongController,
                completeQuizController, new QuizDataAccessObject(), generateFromWrongViewModel));
        frame.revalidate();
        frame.repaint();
    }

    private void handleLogout(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to log out?",
                "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            frame.getContentPane().removeAll();
            frame.add(new StartScreen(frame));
            frame.revalidate();
            frame.repaint();
        }
    }
}
