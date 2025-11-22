package trivia.entity;
import java.time.LocalDateTime;
import java.util.List;

public class QuizAttempt {
    private String attemptId;
    private Quiz quiz;
    private int score;
    private int totalQuestions;
    private List<String> userAnswers;
    private List<Integer> selectedOptionIndices;
    private LocalDateTime completedAt;
    private boolean editable;

    // Constructor
    public QuizAttempt(String attemptId, Quiz quiz, int totalQuestions) {
        this.attemptId = attemptId;
        this.quiz = quiz;
        this.totalQuestions = totalQuestions;
        this.score = 0;
        this.completedAt = LocalDateTime.now();
        this.editable = true;
    }

    // Getters
    public String getAttemptId() {
        return attemptId;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public String getQuizId() {
        return quiz != null ? quiz.getId() : null;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public List<String> getUserAnswers() {
        return userAnswers;
    }

    public List<Integer> getSelectedOptionIndices() {
        return selectedOptionIndices;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public boolean isEditable() {
        return editable;
    }

    // Setters
    public void setScore(int score) {
        this.score = score;
    }

    public void setUserAnswers(List<String> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public void setSelectedOptionIndices(List<Integer> selectedOptionIndices) {
        this.selectedOptionIndices = selectedOptionIndices;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    // Calculate accuracy percentage
    public double getAccuracy() {
        if (totalQuestions == 0) return 0.0;
        return (double) score / totalQuestions * 100.0;
    }
}
