package trivia.use_case.select_quiz;

public interface SelectQuizOutputBoundary {
    void presentSuccess(SelectQuizOutputData outputData);
    void presentFailure(String errorMessage);
}