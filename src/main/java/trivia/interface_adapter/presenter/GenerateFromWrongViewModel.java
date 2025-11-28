package trivia.interface_adapter.presenter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel for UC6.
 * For now we mainly use it to show dialogs; if你们以后要有专门的 Screen，可以复用这里的 state。
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
