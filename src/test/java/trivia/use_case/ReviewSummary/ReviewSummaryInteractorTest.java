package trivia.use_case.ReviewSummary;

import org.junit.jupiter.api.Test;
import trivia.use_case.review_summary.ReviewSummaryInteractor;
import trivia.use_case.review_summary.ReviewSummaryOutputBoundary;
import trivia.use_case.review_summary.ReviewSummaryRequestModel;
import trivia.use_case.review_summary.ReviewSummaryResponseModel;

import static org.junit.jupiter.api.Assertions.*;

class ReviewSummaryInteractorTest {

    //Helper method
    private static class TestPresenter implements ReviewSummaryOutputBoundary {
        ReviewSummaryResponseModel responseModel;

        @Override
        public void presentReviewSummary(ReviewSummaryResponseModel responseModel) {
            this.responseModel = responseModel;
        }
    }

    //Tests
    @Test
    void testInteractorGenerateBasicSummary() {

        //Set up
        TestPresenter presenter = new TestPresenter();
        ReviewSummaryInteractor interactor = new ReviewSummaryInteractor(presenter);

        int score = 10;
        int numberOfQuestions = 15;

        ReviewSummaryRequestModel request = new ReviewSummaryRequestModel(score, numberOfQuestions);

        interactor.generateReviewSummary(request);

        //Assertations
        assertNotNull(presenter.responseModel);
        assertEquals(10, presenter.responseModel.getScore(),
                "Should return the correct Score");
        assertEquals(67, presenter.responseModel.getAccuracy(),
                "Should return the correct Accuracy");

    }

    @Test
    void testInteractorGenerateZeroSummary() {

        //Set up
        TestPresenter presenter = new TestPresenter();
        ReviewSummaryInteractor interactor = new ReviewSummaryInteractor(presenter);

        int score = 0;
        int accuracy = 0;

        ReviewSummaryRequestModel request = new ReviewSummaryRequestModel(score, accuracy);

        interactor.generateReviewSummary(request);

        //Assertations
        assertNotNull(presenter.responseModel);
        assertEquals(0, presenter.responseModel.getScore(),
                "Should return the correct Score");
        assertEquals(0, presenter.responseModel.getAccuracy(),
                "Should return the correct Accuracy");

    }
}
