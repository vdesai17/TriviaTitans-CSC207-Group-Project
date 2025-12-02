package trivia.interface_adapter.presenter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ReviewSummaryViewModel {

    private String score;
    private String accuracy;

    private final PropertyChangeSupport support;

    public ReviewSummaryViewModel() {
        this.score = "";
        this.accuracy = "";
        this.support = new PropertyChangeSupport(this);
    }

    // Getters
    public String getScore() {
        return score;
    }

    public String getAccuracy() {
        return accuracy;
    }

    // Setters with PropertyChange
    public void setScore(String score) {
        String oldScore = this.score;
        this.score = score;
        support.firePropertyChange("score", oldScore, score);
    }

    public void setAccuracy(String accuracy) {
        String oldAccuracy = this.accuracy;
        this.accuracy = accuracy;
        support.firePropertyChange("accuracy", oldAccuracy, accuracy);
    }

    // Observer
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    //notifies property change
    public void firePropertyChanged() {
        support.firePropertyChange("score", null, this.score);
        support.firePropertyChange("accuracy", null, this.accuracy);
    }
}
