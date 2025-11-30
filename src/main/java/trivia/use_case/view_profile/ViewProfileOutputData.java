package trivia.use_case.view_profile;

public class ViewProfileOutputData {
    private final String playerName;
    private final int totalScore;
    private final int totalAttempts;
    private final int rank;
    private final int totalPlayers;

    public ViewProfileOutputData(String playerName, int totalScore, int totalAttempts,
                                 int rank, int totalPlayers) {
        this.playerName = playerName;
        this.totalScore = totalScore;
        this.totalAttempts = totalAttempts;
        this.rank = rank;
        this.totalPlayers = totalPlayers;
    }

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
}