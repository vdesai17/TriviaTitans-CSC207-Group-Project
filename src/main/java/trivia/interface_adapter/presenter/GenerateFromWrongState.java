package trivia.interface_adapter.presenter;

import java.util.ArrayList;
import java.util.List;

/**
 * State object for Use Case 6.
 * The ViewModel holds an instance of this, and UI observes its changes.
 */
public class GenerateFromWrongState {

    private String quizId;
    private List<String> questionTexts = new ArrayList<>();
    private String errorMessage;

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
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
