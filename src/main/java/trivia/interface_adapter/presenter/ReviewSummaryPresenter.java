package trivia.interface_adapter.presenter;

import trivia.use_case.review_summary.ReviewSummaryOutputBoundary;
import trivia.use_case.review_summary.ReviewSummaryResponseModel;

public class ReviewSummaryPresenter implements ReviewSummaryOutputBoundary {

    private final ReviewSummaryViewModel viewModel;

    // Getting viewModel
    public ReviewSummaryPresenter(ReviewSummaryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentReviewSummary(ReviewSummaryResponseModel response) {
        //Format numbers to strings
        String scoreString = "Score: " + response.getScore();
        String accuracyString = "Accuracy: " + response.getAccuracy() + "%";

        //Set view model
        viewModel.setScore(scoreString);
        viewModel.setAccuracy(accuracyString);
    }



}
