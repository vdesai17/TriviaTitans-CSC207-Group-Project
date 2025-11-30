package trivia.use_case.view_profile;

public class ViewProfileInputData {
    private final String playerName;

    public ViewProfileInputData(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}