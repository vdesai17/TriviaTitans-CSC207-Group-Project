package trivia.use_case.review_summary;

public class ReviewSummaryResponseModel {
    private final int score;
    private final double accuracy;

    public ReviewSummaryResponseModel(int score, int accuracy) {
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
