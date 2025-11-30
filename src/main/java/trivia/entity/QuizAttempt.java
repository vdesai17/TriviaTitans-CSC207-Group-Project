package trivia.entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuizAttempt {
    private String attemptId;
    private Quiz quiz;
    private int score;
    private int totalQuestions;
    private List<String> userAnswers;
    private List<Integer> selectedOptionIndices;
    private String completedAt;
    private boolean editable;
    private String userName;

    // Constructor
    public QuizAttempt(String attemptId, Quiz quiz, int totalQuestions, String userName, String completedAt, List<String> userAnswers, int score) {

        this.attemptId = attemptId;
        this.quiz = quiz;
        this.totalQuestions = totalQuestions;
        this.userName = userName;
        this.completedAt = completedAt;
        this.userAnswers = new ArrayList<>(userAnswers);
        this.score = score;
        this.editable = false;
    }


    //old one
    public QuizAttempt(String attemptId, Quiz quiz, int totalQuestions) {
        this(attemptId, quiz, totalQuestions,
                null,
                java.time.LocalDateTime.now().toString(),
                new ArrayList<>(),
                0);
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

    public List<String> getUserAnswers() { return new ArrayList<>(userAnswers); }

    public List<Integer> getSelectedOptionIndices() {
        return selectedOptionIndices;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public boolean isEditable() {
        return editable;
    }

    public String getUserName() { return userName; }

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

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // Calculate accuracy percentage
    public double getAccuracy() {
        if (totalQuestions == 0) return 0.0;
        return (double) score / totalQuestions * 100.0;
    }
}
