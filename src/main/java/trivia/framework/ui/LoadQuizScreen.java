package trivia.framework.ui;

import trivia.entity.Player;
import trivia.entity.Quiz;
import trivia.entity.Question;
import trivia.interface_adapter.dao.QuizDataAccessObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * LoadQuizScreen — allows players to view and open their saved custom quizzes.
 * Displays quizzes associated with the logged-in player, stored via QuizDataAccessObject.
 */
public class LoadQuizScreen extends JPanel {
    private final JFrame frame;
    private final Player player;
    private final QuizDataAccessObject quizDAO;

    public LoadQuizScreen(JFrame frame, Player player) {
        this.frame = frame;
        this.player = player;
        this.quizDAO = new QuizDataAccessObject();

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Your Saved Quizzes", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // Fetch quizzes belonging to this player
        List<Quiz> playerQuizzes = quizDAO.getQuizzesByPlayer(player.getPlayerName());
        DefaultListModel<String> listModel = new DefaultListModel<>();

        if (playerQuizzes == null || playerQuizzes.isEmpty()) {
            listModel.addElement("No saved quizzes found.");
        } else {
            for (Quiz q : playerQuizzes) {
                listModel.addElement(q.getTitle() + "  (" + q.getCategory() + ")");
            }
        }

        // Display quiz titles in a scrollable list
        JList<String> quizList = new JList<>(listModel);
        quizList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quizList.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(quizList);
        add(scrollPane, BorderLayout.CENTER);

        // --- Open Selected Quiz ---
        JButton openButton = new JButton("Open Selected Quiz");
        openButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        openButton.addActionListener((ActionEvent e) -> {
            int index = quizList.getSelectedIndex();
            if (playerQuizzes == null || playerQuizzes.isEmpty() || index == -1) {
                JOptionPane.showMessageDialog(frame,
                        "Please select a valid quiz.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Quiz selected = playerQuizzes.get(index);
                List<Question> questions = selected.getQuestions();

                if (questions == null || questions.isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "This quiz has no questions.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                frame.getContentPane().removeAll();
                frame.add(new QuizScreen(frame, questions, player)); // ✅ fixed constructor
                frame.revalidate();
                frame.repaint();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Failed to load quiz: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- Back Button ---
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        backButton.addActionListener((ActionEvent e) -> {
            frame.getContentPane().removeAll();
            frame.add(new HomeScreen(frame, player, null));
            frame.revalidate();
            frame.repaint();
        });

        // --- Layout for buttons ---
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));
        bottomPanel.add(backButton);
        bottomPanel.add(openButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}
