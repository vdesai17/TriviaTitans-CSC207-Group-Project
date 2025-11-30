package trivia.use_case.start_quiz;

public class StartQuizInputData {
    private final String quizId;
    private final String playerName;

    public StartQuizInputData(String quizId, String playerName) {
        this.quizId = quizId;
        this.playerName = playerName;
    }

    public String getQuizId() { return quizId; }
    public String getPlayerName() { return playerName; }
}