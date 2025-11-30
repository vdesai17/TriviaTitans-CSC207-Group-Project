package trivia.interface_adapter.presenter;

import trivia.entity.Question;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class SelectQuizViewModel {

    private final PropertyChangeSupport support;

    private List<Question> questions;
    private String errorMessage;

    public SelectQuizViewModel() {
        this.support = new PropertyChangeSupport(this);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void firePropertyChanged() {
        support.firePropertyChange("selectQuiz", null, this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}