package trivia.interface_adapter.presenter;

import trivia.use_case.review_summary.ReviewSummaryOutputBoundary;
import trivia.use_case.review_summary.ReviewSummaryResponseModel;

public class ReviewSummaryPresenter implements ReviewSummaryOutputBoundary {

    private final ReviewSummaryViewModel viewModel;

    public ReviewSummaryPresenter(ReviewSummaryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentReviewSummary(ReviewSummaryResponseModel response) {

        viewModel.setScore("Score: " + response.getScore());
        viewModel.setAccuracy("Accuracy: " + response.getAccuracy() + "%");

        viewModel.firePropertyChanged();
    }
}




