package trivia.interface_adapter.controller;

import trivia.use_case.review_quiz.ReviewQuizInputBoundary;
import trivia.use_case.review_quiz.ReviewQuizRequestModel;

import java.util.List;

/**
 * Controller for Use Case 3: Review & Edit Past Questions
 * Now includes redo quiz functionality following Clean Architecture
 * Handles user actions from the UI and delegates to the use case
 */
public class ReviewController {

    private final ReviewQuizInputBoundary inputBoundary;

    public ReviewController(ReviewQuizInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    /**
     * Load and display all past quiz attempts for a player
     */
    public void viewPastQuizzes(String playerName) {
        inputBoundary.viewPastQuizzes(playerName);
    }

    /**
     * Open a specific quiz attempt to view details
     */
    public void openAttempt(String attemptId) {
        inputBoundary.openAttempt(attemptId);
    }

    /**
     * Save edited answers for a quiz attempt
     */
    public void saveEditedAnswers(ReviewQuizRequestModel requestModel) {
        inputBoundary.saveEditedAnswers(requestModel);
    }

    /**
     * ✅ NEW: Prepare to redo a quiz from a past attempt
     * This follows Clean Architecture by going through the use case layer
     */
    public void prepareRedoQuiz(String attemptId) {
        inputBoundary.prepareRedoQuiz(attemptId);
    }
}