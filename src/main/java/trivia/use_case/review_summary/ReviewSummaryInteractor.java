package trivia.use_case.review_summary;

import trivia.entity.QuizAttempt;

public class ReviewSummaryInteractor implements ReviewSummaryInputBoundary {

    private final ReviewSummaryOutputBoundary output;

    public ReviewSummaryInteractor(ReviewSummaryOutputBoundary output) {
        this.output = output;
    }
    @Override
    public void generateReviewSummary(ReviewSummaryRequestModel request) {
        QuizAttempt quizAttempt = request.getQuizAttempt();

        ReviewSummaryResponseModel response = new ReviewSummaryResponseModel(quizAttempt.getScore,
                quizAttempt.getAccuracy);

        output.presentReviewSummary(response);
    }
}
