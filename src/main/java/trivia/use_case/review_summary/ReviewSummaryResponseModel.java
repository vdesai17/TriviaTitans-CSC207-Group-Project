package trivia.use_case.review_summary;

public class ReviewSummaryResponseModel {
    private final int score;
    private final double accuracy;
    private final String quizTitle;
    private final String quizId;

    public ReviewSummaryResponseModel(int score, double accuracy) {
        this.score = score;
        this.accuracy = accuracy;
        this.quizTitle = null;
        this.quizId = null;
    }

    public int getScore() {
        return score;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public String getQuizId() {
        return quizId;
    }
}
