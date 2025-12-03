package trivia.use_case.select_quiz;

/**
 * Input boundary (use case interface) for the "Select Quiz" use case.
 *
 * <p>This is the interface that the Controller depends on. The Controller
 * calls {@link #execute(SelectQuizInputData)} when the user has chosen
 * a category, difficulty, and amount of questions.
 *
 * <p>The actual implementation is {@link SelectQuizInteractor}.
 * Defining this boundary keeps the UI layer independent from the
 * concrete interactor implementation.
 */

public interface SelectQuizInputBoundary {

    /**
     * Executes the "Select Quiz" use case with the given input data.
     *
     * @param inputData value object containing categoryId, difficulty,
     *                  and amount chosen by the user
     */
    void execute(SelectQuizInputData inputData);
}