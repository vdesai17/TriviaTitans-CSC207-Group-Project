package trivia.entity;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String playerName;
    private int score;
    private List<QuizAttempt> pastAttempts = new ArrayList<>();

    public Player(String playerName) {
        this.playerName = playerName;
        this.score = 0;
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public List<QuizAttempt> getPastAttempts() { return pastAttempts; }

    public void addAttempt(QuizAttempt attempt) {
        pastAttempts.add(attempt);
    }
}
