package trivia.interface_adapter.controller;

import trivia.entity.QuizAttempt;
import trivia.use_case.review_summary.ReviewSummaryInputBoundary;
import trivia.use_case.review_summary.ReviewSummaryRequestModel;

public class ReviewSummaryController {

    // Controller input
    private final ReviewSummaryInputBoundary input;

    public ReviewSummaryController(ReviewSummaryInputBoundary input) {
        this.input = input;
    }

    //Generates the review summary from the given quiz attempt
    public void generateSummary(QuizAttempt quizAttempt) {
        ReviewSummaryRequestModel requestModel = new ReviewSummaryRequestModel(quizAttempt);
        input.generateReviewSummary(requestModel);
    }
}
