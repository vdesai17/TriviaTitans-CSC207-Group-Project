package trivia.use_case.review_summary;

public class ReviewSummaryInteractor implements ReviewSummaryInputBoundary {

    private final ReviewSummaryOutputBoundary output;

    public ReviewSummaryInteractor(ReviewSummaryOutputBoundary output) {

        this.output = output;
    }

    //generate Summary
    @Override
    public void generateReviewSummary(ReviewSummaryRequestModel request) {
        int score = request.getScore();
        int accuracy = request.getAccuracy();

        //Set Response model
        ReviewSummaryResponseModel response = new ReviewSummaryResponseModel(score, accuracy);

        //Present Response model
        output.presentReviewSummary(response);
    }
}
