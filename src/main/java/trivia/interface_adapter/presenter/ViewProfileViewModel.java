package trivia.interface_adapter.presenter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ViewProfileViewModel {

    private final PropertyChangeSupport support;

    private String playerName;
    private int totalScore;
    private int totalAttempts;
    private int rank;
    private int totalPlayers;

    public ViewProfileViewModel() {
        this.support = new PropertyChangeSupport(this);
    }

    // Getters
    public String getPlayerName() {
        return playerName;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public int getRank() {
        return rank;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    // Setters
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    // Property change support
    public void firePropertyChanged() {
        support.firePropertyChange("profile", null, this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}