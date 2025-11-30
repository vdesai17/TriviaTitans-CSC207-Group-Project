package trivia.use_case.review_quiz;

import trivia.entity.Quiz;
import java.time.LocalDateTime;
import java.util.List;

public class ReviewQuizResponseModel {

    private List<PastQuizSummary> pastQuizzes;

    private String attemptId;
    private String quizTitle;
    private List<QuestionRow> questions;
    private boolean editingEnabled;

    private String message; // "No past quizzes found", "Changes saved", etc.

    // ✅ NEW: For redo functionality
    private Quiz quizToRedo;

    public List<PastQuizSummary> getPastQuizzes() {
        return pastQuizzes;
    }

    public void setPastQuizzes(List<PastQuizSummary> pastQuizzes) {
        this.pastQuizzes = pastQuizzes;
    }

    public String getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public List<QuestionRow> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionRow> questions) {
        this.questions = questions;
    }

    public boolean isEditingEnabled() {
        return editingEnabled;
    }

    public void setEditingEnabled(boolean editingEnabled) {
        this.editingEnabled = editingEnabled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // ✅ NEW: Getters/Setters for redo
    public Quiz getQuizToRedo() {
        return quizToRedo;
    }

    public void setQuizToRedo(Quiz quizToRedo) {
        this.quizToRedo = quizToRedo;
    }

    // inner DTOs --------------

    public static class PastQuizSummary {
        private final String attemptId;
        private final String quizTitle;
        private final int score;
        private final String completedAt;

        public PastQuizSummary(String attemptId, String quizTitle, int score, String completedAt) {
            this.attemptId = attemptId;
            this.quizTitle = quizTitle;
            this.score = score;
            this.completedAt = completedAt;
        }

        public String getAttemptId() {
            return attemptId;
        }

        public String getQuizTitle() {
            return quizTitle;
        }

        public int getScore() {
            return score;
        }

        public String getCompletedAt() {
            return completedAt;
        }
    }

    public static class QuestionRow {
        private final String questionText;
        private final List<String> options;
        private final int correctIndex;
        private final int selectedIndex;

        public QuestionRow(String questionText,
                           List<String> options,
                           int correctIndex,
                           int selectedIndex) {
            this.questionText = questionText;
            this.options = options;
            this.correctIndex = correctIndex;
            this.selectedIndex = selectedIndex;
        }

        public String getQuestionText() {
            return questionText;
        }

        public List<String> getOptions() {
            return options;
        }

        public int getCorrectIndex() {
            return correctIndex;
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }
    }
}