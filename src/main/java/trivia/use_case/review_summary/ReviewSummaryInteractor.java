package trivia.use_case.review_summary;

import trivia.entity.QuizAttempt;

public class ReviewSummaryInteractor implements ReviewSummaryInputBoundary {

    private final ReviewSummaryOutputBoundary output;

    public ReviewSummaryInteractor(ReviewSummaryOutputBoundary output) {
        this.output = output;
    }
    @Override
    public void generateReviewSummary(ReviewSummaryRequestModel request) {
        int score = request.getScore();
        double accuracy = request.getAccuracy();

        ReviewSummaryResponseModel response = new ReviewSummaryResponseModel(score, accuracy);

        output.presentReviewSummary(response);
    }
}
