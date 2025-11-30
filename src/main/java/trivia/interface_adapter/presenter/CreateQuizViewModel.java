package trivia.interface_adapter.presenter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class CreateQuizViewModel {
    public static final String CREATE_QUIZ_PROPERTY = "createQuiz";

    private final PropertyChangeSupport support;

    // 保存给 UI 用的数据
    private boolean success;
    private String errorMessage;
    private String quizId;

    public CreateQuizViewModel() {
        this.support = new PropertyChangeSupport(this);
    }

    // ====== 状态字段的 getter / setter（不直接 fire 事件）======

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    // ====== 事件相关 ======

    /** Presenter 在所有 setter 调完之后，调用一次 firePropertyChanged() 通知 UI */
    public void firePropertyChanged() {
        // 这里 newValue 不用传具体对象，view 可以直接从 viewModel 读字段
        support.firePropertyChange(CREATE_QUIZ_PROPERTY, null, null);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
