package trivia.use_case.select_quiza;

public interface SelectQuizOutputBoundary {
    void presentSuccess(SelectQuizOutputData outputData);
    void presentFailure(String errorMessage);
}