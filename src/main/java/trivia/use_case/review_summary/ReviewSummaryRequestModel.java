package trivia.use_case.review_summary;

import trivia.entity.QuizAttempt;

public class ReviewSummaryRequestModel {
    private final QuizAttempt quizAttempt;

    public ReviewSummaryRequestModel(QuizAttempt quizAttempt) {
        this.quizAttempt = quizAttempt;
    }

    public QuizAttempt getQuizAttempt() {
        return quizAttempt;
    }
}
