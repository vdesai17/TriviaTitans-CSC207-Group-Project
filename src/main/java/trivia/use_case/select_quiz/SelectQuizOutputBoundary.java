package trivia.use_case.select_quiz;

/**
 * Output boundary (presenter interface) for the "Select Quiz" use case.
 *
 * <p>The interactor calls this interface to report either:
 * <ul>
 *     <li>a successful result with a list of questions, or</li>
 *     <li>a failure with an error message</li>
 * </ul>
 *
 * <p>The Presenter implements this interface and converts the raw
 * {@link SelectQuizOutputData} into a ViewModel that the UI layer
 * (e.g., a Swing screen) can display.
 */
public interface SelectQuizOutputBoundary {

    /**
     * Called by the interactor when the quiz questions are successfully loaded.
     *
     * @param outputData use case output containing the list of questions
     */
    void presentSuccess(SelectQuizOutputData outputData);

    /**
     * Called by the interactor when something goes wrong
     * (validation error, API error, etc.).
     *
     * @param errorMessage a human-readable error description
     */
    void presentFailure(String errorMessage);
}