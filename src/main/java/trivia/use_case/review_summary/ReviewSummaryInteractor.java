package trivia.use_case.review_summary;

public class ReviewSummaryInteractor implements ReviewSummaryInputBoundary {

    private final ReviewSummaryOutputBoundary output;

    public ReviewSummaryInteractor(ReviewSummaryOutputBoundary output) {
        this.output = output;
    }

    @Override
    public void generateReviewSummary(ReviewSummaryRequestModel request) {

        int score = request.getScore();
        int numberOfQuestions = request.getNumberOfQuestions();

        int accuracy = (numberOfQuestions == 0)
                ? 0
                : Math.round((float) score * 100 / numberOfQuestions);

        ReviewSummaryResponseModel response =
                new ReviewSummaryResponseModel(score, accuracy);

        output.presentReviewSummary(response);
    }
}
