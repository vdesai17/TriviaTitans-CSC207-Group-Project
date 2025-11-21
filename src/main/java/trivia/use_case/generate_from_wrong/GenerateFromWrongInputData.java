package trivia.use_case.generate_from_wrong;

/**
 * Input data for Use Case 6.
 */
public class GenerateFromWrongInputData {

    private final String playerName;
    private final int requestedNumberOfQuestions;

    public GenerateFromWrongInputData(String playerName,
                                      int requestedNumberOfQuestions) {
        this.playerName = playerName;
        this.requestedNumberOfQuestions = requestedNumberOfQuestions;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getRequestedNumberOfQuestions() {
        return requestedNumberOfQuestions;
    }
}
