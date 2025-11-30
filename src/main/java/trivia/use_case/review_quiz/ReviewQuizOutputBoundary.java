package trivia.use_case.review_quiz;

/**
 * Output boundary for Review Quiz use case (UC3)
 * Now includes redo quiz presentation following Clean Architecture
 */
public interface ReviewQuizOutputBoundary {
    void presentPastQuizList(ReviewQuizResponseModel responseModel);
    void presentQuizAttempt(ReviewQuizResponseModel responseModel);
    void presentSaveResult(ReviewQuizResponseModel responseModel);
    
    // ✅ NEW: Present redo quiz to UI
    void presentRedoQuiz(ReviewQuizResponseModel responseModel);
}