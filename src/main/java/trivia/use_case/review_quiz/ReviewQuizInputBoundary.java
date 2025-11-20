package trivia.use_case.review_quiz;

public interface ReviewQuizInputBoundary {
    void viewPastQuizzes (String playerName);
    void openAttempt(String attemptId);
    void saveEditedAnswers(ReviewQuizRequestModel requestModel);
}