package trivia.use_case.load_quiz;

public interface LoadQuizOutputBoundary {
    void presentQuizzes(LoadQuizOutputData outputData);
    void presentError(String errorMessage);
}