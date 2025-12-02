package trivia.use_case.review_summary;


public class ReviewSummaryRequestModel {
    private final int score;
    private final int numberOfQuestions;

    public ReviewSummaryRequestModel(int score, int numberOfQuestions) {
        this.score = score;
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getScore() { return score; }
    public int getNumberOfQuestions() { return numberOfQuestions; }
}
