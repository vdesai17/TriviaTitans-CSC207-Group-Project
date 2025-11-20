package trivia.use_case.review_quiz;

import java.util.List;

public class ReviewQuizResponseModel {

    private String message;
    private List<PastQuizSummary> pastQuizzes;
    private boolean editing;
    private String attemptId;
    private String quizTitle;
    private List<QuestionRow> questions;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setEditingEnabled(Boolean editingEnabled) {
        this.editing = editingEnabled;
    }

    public boolean isEditingEnabled() {
        return editing;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public String getAttemptId() {
        return attemptId;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuestions(List<QuestionRow> questions) {
        this.questions = questions;
    }

    public List<QuestionRow> getQuestions() {
        return questions;
    }

    public void setPastQuizzes(List<PastQuizSummary> pastQuizzes) {
        this.pastQuizzes = pastQuizzes;
    }

    public List<PastQuizSummary> getPastQuizzes() {
        return pastQuizzes;
    }

    public static class PastQuizSummary {
        private final String quizTitle;
        private final String attemptId;
        private final int score;

        public PastQuizSummary(String quizTitle, String attemptId, int score) {
            this.quizTitle = quizTitle;
            this.attemptId = attemptId;
            this.score = score;
        }

        public String getQuizTitle() {
            return quizTitle;
        }

        public int getScore() {
            return score;
        }

        public String getAttemptId() {
            return attemptId;
        }
    }

    public static class QuestionRow {

        private final String questionText;
        private final List<String> options;
        private final int correctOptionIndex;
        private final int index;

        public QuestionRow(String questionText, List<String> options, int correctOptionIndex, int index) {
            this.questionText = questionText;
            this.options = options;
            this.correctOptionIndex = correctOptionIndex;
            this.index = index;
        }

        public String getQuestionText() {
            return questionText;
        }

        public List<String> getOptions() {
            return options;
        }

        public int getCorrectOptionIndex() {
            return correctOptionIndex;
        }

        public int getIndex() {
            return index;
        }
    }
}