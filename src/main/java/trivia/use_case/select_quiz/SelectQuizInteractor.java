package trivia.use_case.select_quiz;

import trivia.entity.Question;
import java.util.List;

/**
 * Interactor (use case implementation) for "Select Quiz".
 *
 * Responsibilities:
 *
 *     Validate the incoming input data (category, difficulty, amount)
 *     Ask the data access layer to fetch questions from the API
 *     Wrap the result in {@link SelectQuizOutputData}
 *     Report success or failure through {@link SelectQuizOutputBoundary}
 *
 *
 * Clean Architecture principles:
 *     Depends only on the input/output boundaries and entities
 *     Does not know anything about Swing, API implementation, or database
 *     All side effects are delegated to the outer layers via interfaces
 */
public class SelectQuizInteractor implements SelectQuizInputBoundary {

    /** Gateway used to fetch questions from an external source (e.g., Open Trivia DB API). */
    private final SelectQuizAPIDataAccessInterface apiDataAccess;

    /** Presenter used to send success/failure results back to the UI layer. */
    private final SelectQuizOutputBoundary presenter;

    /**
     * Constructs a new interactor for the "Select Quiz" use case.
     *
     * @param apiDataAccess data access gateway for fetching questions
     * @param presenter     output boundary to present the result to the user
     */
    public SelectQuizInteractor(SelectQuizAPIDataAccessInterface apiDataAccess,
                                SelectQuizOutputBoundary presenter) {
        this.apiDataAccess = apiDataAccess;
        this.presenter = presenter;
    }

    /**
     * Main use case execution method.
     *
     * High-level steps:
     *     Validate the {@code inputData}
     *     Ask {@code apiDataAccess} to fetch questions
     *     Validate the returned question list
     *     Notify {@code presenter} of success or failure
     *
     *
     * @param inputData value object containing categoryId, difficulty, and amount
     */
    @Override
    public void execute(SelectQuizInputData inputData) {
        try {
            // 1. Null-check the input object itself
            if (inputData == null) {
                presenter.presentFailure("Invalid input data.");
                return;
            }

            // 2. Extract raw values from the input data
            String categoryId = inputData.getCategoryId();
            String difficulty = inputData.getDifficulty();
            int amount = inputData.getAmount();

            // 3. Validate each field with domain-specific rules

            // Category must not be null or empty
            if (categoryId == null || categoryId.isEmpty()) {
                presenter.presentFailure("Category ID cannot be empty.");
                return;
            }

            // Difficulty must not be null or empty
            if (difficulty == null || difficulty.isEmpty()) {
                presenter.presentFailure("Difficulty cannot be empty.");
                return;
            }

            // Amount must be a positive integer
            if (amount <= 0) {
                presenter.presentFailure("Amount must be positive.");
                return;
            }

            // 4. Call the data access interface to fetch questions from an external API
            //    Note: the interactor does NOT know how the API call is implemented.
            List<Question> questions = apiDataAccess.fetchQuestions(categoryId, difficulty, amount);

            // 5. Check that we actually received questions back
            if (questions == null || questions.isEmpty()) {
                presenter.presentFailure("No questions found. Try another combination.");
                return;
            }

            // 6. Wrap the list of entities in an output data object and send it to the presenter
            SelectQuizOutputData outputData = new SelectQuizOutputData(questions);
            presenter.presentSuccess(outputData);

        } catch (Exception e) {
            // 7. Any unexpected error is reported as a failure message to the presenter
            presenter.presentFailure("Failed to load questions: " + e.getMessage());
        }
    }
}
