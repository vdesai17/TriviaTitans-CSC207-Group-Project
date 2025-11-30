package trivia.use_case.load_quiz;

import java.util.List;

public class LoadQuizOutputData {
    private final List<QuizSummary> quizzes;

    public LoadQuizOutputData(List<QuizSummary> quizzes) {
        this.quizzes = quizzes;
    }

    public List<QuizSummary> getQuizzes() {
        return quizzes;
    }

    public static class QuizSummary {
        private final String quizId;
        private final String title;
        private final String category;
        private final String createdAt;

        public QuizSummary(String quizId, String title, String category, String createdAt) {
            this.quizId = quizId;
            this.title = title;
            this.category = category;
            this.createdAt = createdAt;
        }

        public String getQuizId() { return quizId; }
        public String getTitle() { return title; }
        public String getCategory() { return category; }
        public String getCreatedAt() { return createdAt; }
    }
}