package trivia.use_case.generate_from_wrong;

import java.util.List;

/**
 * Output data for UC6.
 */
public class GenerateFromWrongOutputData {
    private final String quizId;
    private final int numberOfQuestions;
    private final List<String> questionTexts;

    public GenerateFromWrongOutputData(String quizId,
                                       int numberOfQuestions,
                                       List<String> questionTexts) {
        this.quizId = quizId;
        this.numberOfQuestions = numberOfQuestions;
        this.questionTexts = questionTexts;
    }

    public String getQuizId() {
        return quizId;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public List<String> getQuestionTexts() {
        return questionTexts;
    }
}
