package trivia.interface_adapter.presenter;

import trivia.entity.Player;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class LoginViewModel {
    public static final String LOGIN_SUCCESS_PROPERTY = "loginSuccess";
    public static final String LOGIN_FAILURE_PROPERTY = "loginFailure";

    private final PropertyChangeSupport support;
    private Player loggedInPlayer;
    private String errorMessage;

    public LoginViewModel() {
        this.support = new PropertyChangeSupport(this);
    }

    public void setLoginSuccess(Player player) {
        this.loggedInPlayer = player;
        support.firePropertyChange(LOGIN_SUCCESS_PROPERTY, null, player);
    }

    public void setLoginFailure(String errorMessage) {
        this.errorMessage = errorMessage;
        support.firePropertyChange(LOGIN_FAILURE_PROPERTY, null, errorMessage);
    }

    public Player getLoggedInPlayer() {
        return loggedInPlayer;
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