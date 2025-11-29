package trivia.entity;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String playerName;
    private String password; // added for login authentication
    private int score;
    private List<QuizAttempt> pastAttempts = new ArrayList<>();

    // Constructor for registration (new player)
    public Player(String playerName, String password) {
        this.playerName = playerName;
        this.password = password;
        this.score = 0;
    }

    // Overloaded constructor (for compatibility with existing code)
    public Player(String playerName) {
        this(playerName, ""); // default empty password for older flow
    }

    // Getters and setters
    public String getPlayerName() { return playerName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public List<QuizAttempt> getPastAttempts() { return pastAttempts; }
    public void addAttempt(QuizAttempt attempt) { pastAttempts.add(attempt); }

    // Simple password check for login
    public boolean verifyPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }
}

