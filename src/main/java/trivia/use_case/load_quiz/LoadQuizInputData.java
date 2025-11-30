package trivia.use_case.load_quiz;

public class LoadQuizInputData {
    private final String playerName;

    public LoadQuizInputData(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}