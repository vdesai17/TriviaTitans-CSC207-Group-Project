package trivia.use_case.review_quiz;

import java.util.List;

public class ReviewQuizRequestModel {
    private final String attemptId;
    private final String playerName;
    private final List<Integer> updatedSelectedOptionIndices;

    public ReviewQuizRequestModel(String attemptId,
                                  String playerName,
                                  List<Integer> updatedSelectedOptionIndices) {
        this.attemptId = attemptId;
        this.playerName = playerName;
        this.updatedSelectedOptionIndices = updatedSelectedOptionIndices;
    }

    public String getAttemptId() {
        return attemptId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public List<Integer> getUpdatedSelectedOptionIndices() {
        return updatedSelectedOptionIndices;
    }
}
