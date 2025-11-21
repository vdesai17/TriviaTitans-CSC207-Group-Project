package trivia.interface_adapter.presenter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel for Use Case 6.
 * Holds a GenerateFromWrongState and notifies listeners when it changes.
 */
public class GenerateFromWrongViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private GenerateFromWrongState state = new GenerateFromWrongState();

    public GenerateFromWrongState getState() {
        return state;
    }

    public void setState(GenerateFromWrongState state) {
        this.state = state;
    }

    public void fireStateChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
