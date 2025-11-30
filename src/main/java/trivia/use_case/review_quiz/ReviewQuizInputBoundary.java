package trivia.use_case.review_quiz;

/**
 * Input boundary for Review Quiz use case (UC3)
 * Now includes redo quiz functionality following Clean Architecture
 */
public interface ReviewQuizInputBoundary {
    void viewPastQuizzes(String playerName);
    void openAttempt(String attemptId);
    void saveEditedAnswers(ReviewQuizRequestModel requestModel);
    
    // ✅ NEW: Redo quiz functionality
    void prepareRedoQuiz(String attemptId);
}