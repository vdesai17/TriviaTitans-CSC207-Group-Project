package trivia.interface_adapter.presenter;

import trivia.use_case.load_quiz.LoadQuizOutputData;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class LoadQuizViewModel {
    public static final String QUIZZES_PROPERTY = "quizzes";
    public static final String ERROR_PROPERTY = "error";

    private final PropertyChangeSupport support;
    private List<LoadQuizOutputData.QuizSummary> quizzes;
    private String errorMessage;

    public LoadQuizViewModel() {
        this.support = new PropertyChangeSupport(this);
    }

    public void setQuizzes(List<LoadQuizOutputData.QuizSummary> quizzes) {
        this.quizzes = quizzes;
        support.firePropertyChange(QUIZZES_PROPERTY, null, quizzes);
    }

    public void setError(String errorMessage) {
        this.errorMessage = errorMessage;
        support.firePropertyChange(ERROR_PROPERTY, null, errorMessage);
    }

    public List<LoadQuizOutputData.QuizSummary> getQuizzes() {
        return quizzes;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}