package trivia.use_case.review_summary;

import trivia.entity.QuizAttempt;

public class ReviewSummaryRequestModel {
    private final int score;
    private final double accuracy;

    public ReviewSummaryRequestModel(int score, int accuracy) {
        this.score = score;
        this.accuracy = accuracy;
    }

    // Getters
    public int getScore() {
        return score;
    }

    public double getAccuracy() {
        return accuracy;
    }
}
