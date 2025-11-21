package trivia.use_case.generate_from_wrong;

import java.util.List;

/**
 * Output data for Use Case 6.
 */
public class GenerateFromWrongOutputData {

    private final String quizId;
    private final List<String> questionTexts;
    private final int requestedNumberOfQuestions;
    private final int totalAvailableWrongQuestions;

    public GenerateFromWrongOutputData(String quizId,
                                       List<String> questionTexts,
                                       int requestedNumberOfQuestions,
                                       int totalAvailableWrongQuestions) {
        this.quizId = quizId;
        this.questionTexts = questionTexts;
        this.requestedNumberOfQuestions = requestedNumberOfQuestions;
        this.totalAvailableWrongQuestions = totalAvailableWrongQuestions;
    }

    public String getQuizId() {
        return quizId;
    }

    public List<String> getQuestionTexts() {
        return questionTexts;
    }

    public int getRequestedNumberOfQuestions() {
        return requestedNumberOfQuestions;
    }

    public int getTotalAvailableWrongQuestions() {
        return totalAvailableWrongQuestions;
    }
}
