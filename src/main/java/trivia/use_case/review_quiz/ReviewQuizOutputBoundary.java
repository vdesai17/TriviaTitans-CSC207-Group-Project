package trivia.use_case.review_quiz;

public interface ReviewQuizOutputBoundary {
    void presentPastQuizList(ReviewQuizResponseModel responseModel);
    void presentQuizAttempt(ReviewQuizResponseModel responseModel);
    void presentSaveResult(ReviewQuizResponseModel responseModel);
}
