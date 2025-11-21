package trivia.use_case.generate_from_wrong;

/**
 * Output boundary for Use Case 6.
 */
public interface GenerateFromWrongOutputBoundary {

    void prepareSuccessView(GenerateFromWrongOutputData response);

    void prepareFailView(String errorMessage);
}
