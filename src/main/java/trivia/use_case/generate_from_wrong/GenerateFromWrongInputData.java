package trivia.use_case.generate_from_wrong;

/**
 * Input data for UC6.
 */
public class GenerateFromWrongInputData {
    private final String playerName;
    private final int requestedNumber;

    public GenerateFromWrongInputData(String playerName, int requestedNumber) {
        this.playerName = playerName;
        this.requestedNumber = requestedNumber;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getRequestedNumber() {
        return requestedNumber;
    }
}
