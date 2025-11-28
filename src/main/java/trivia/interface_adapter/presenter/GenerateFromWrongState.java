package trivia.interface_adapter.presenter;

import java.util.ArrayList;
import java.util.List;

/**
 * State object for UC6 ViewModel.
 */
public class GenerateFromWrongState {
    private String quizId;
    private int requestedNumber;
    private int totalAvailableWrongQuestions;
    private List<String> questionTexts = new ArrayList<>();
    private String errorMessage;

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public int getRequestedNumber() {
        return requestedNumber;
    }

    public void setRequestedNumber(int requestedNumber) {
        this.requestedNumber = requestedNumber;
    }

    public int getTotalAvailableWrongQuestions() {
        return totalAvailableWrongQuestions;
    }

    public void setTotalAvailableWrongQuestions(int totalAvailableWrongQuestions) {
        this.totalAvailableWrongQuestions = totalAvailableWrongQuestions;
    }

    public List<String> getQuestionTexts() {
        return questionTexts;
    }

    public void setQuestionTexts(List<String> questionTexts) {
        this.questionTexts = questionTexts;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
