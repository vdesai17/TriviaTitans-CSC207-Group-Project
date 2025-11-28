package trivia.use_case.generate_from_wrong;

public interface GenerateFromWrongOutputBoundary {

    void prepareSuccessView(GenerateFromWrongOutputData response);

    void prepareFailView(String errorMessage);
}
