package trivia.interface_adapter.presenter;

import trivia.entity.Quiz;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for Use Case 3: Review & Edit Past Questions
 * Now includes quiz to redo following Clean Architecture
 * Holds the state for the PastQuizScreen UI
 */
public class PastQuizViewModel {

    private final PropertyChangeSupport support;

    // State
    private List<PastQuizSummaryViewModel> pastQuizzes;
    private String currentAttemptId;
    private String quizTitle;
    private List<QuestionRowViewModel> questions;
    private boolean editingEnabled;
    private String message;
    
    // ✅ NEW: Quiz to redo
    private Quiz quizToRedo;

    public PastQuizViewModel() {
        this.support = new PropertyChangeSupport(this);
        this.pastQuizzes = new ArrayList<>();
        this.questions = new ArrayList<>();
        this.editingEnabled = false;
        this.message = "";
        this.quizToRedo = null;
    }

    // PropertyChangeListener support
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this);
    }

    // Getters
    public List<PastQuizSummaryViewModel> getPastQuizzes() {
        return pastQuizzes;
    }

    public String getCurrentAttemptId() {
        return currentAttemptId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public List<QuestionRowViewModel> getQuestions() {
        return questions;
    }

    public boolean isEditingEnabled() {
        return editingEnabled;
    }

    public String getMessage() {
        return message;
    }

    // ✅ NEW: Getter for quiz to redo
    public Quiz getQuizToRedo() {
        return quizToRedo;
    }

    // Setters (these trigger UI updates)
    public void setPastQuizzes(List<PastQuizSummaryViewModel> pastQuizzes) {
        this.pastQuizzes = pastQuizzes;
        firePropertyChanged();
    }

    public void setCurrentAttemptId(String currentAttemptId) {
        this.currentAttemptId = currentAttemptId;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public void setQuestions(List<QuestionRowViewModel> questions) {
        this.questions = questions;
        firePropertyChanged();
    }

    public void setEditingEnabled(boolean editingEnabled) {
        this.editingEnabled = editingEnabled;
        firePropertyChanged();
    }

    public void setMessage(String message) {
        this.message = message;
        firePropertyChanged();
    }

    // ✅ NEW: Setter for quiz to redo (triggers property change)
    public void setQuizToRedo(Quiz quizToRedo) {
        Quiz oldQuiz = this.quizToRedo;
        this.quizToRedo = quizToRedo;
        // Fire property change with specific property name so UI can listen
        support.firePropertyChange("quizToRedo", oldQuiz, quizToRedo);
        firePropertyChanged();
    }

    // Inner ViewModels (UI-friendly data structures)
    
    public static class PastQuizSummaryViewModel {
        private final String attemptId;
        private final String quizTitle;
        private final int score;
        private final String completedAt;

        public PastQuizSummaryViewModel(String attemptId, String quizTitle, 
                                       int score, String completedAt) {
            this.attemptId = attemptId;
            this.quizTitle = quizTitle;
            this.score = score;
            this.completedAt = completedAt;
        }

        public String getAttemptId() { return attemptId; }
        public String getQuizTitle() { return quizTitle; }
        public int getScore() { return score; }
        public String getCompletedAt() { return completedAt; }

        @Override
        public String toString() {
            return String.format("%s - Score: %d - %s", quizTitle, score, completedAt);
        }
    }

    public static class QuestionRowViewModel {
        private final String questionText;
        private final List<String> options;
        private final int correctIndex;
        private final int selectedIndex;

        public QuestionRowViewModel(String questionText, List<String> options,
                                   int correctIndex, int selectedIndex) {
            this.questionText = questionText;
            this.options = options;
            this.correctIndex = correctIndex;
            this.selectedIndex = selectedIndex;
        }

        public String getQuestionText() { return questionText; }
        public List<String> getOptions() { return options; }
        public int getCorrectIndex() { return correctIndex; }
        public int getSelectedIndex() { return selectedIndex; }
    }
}