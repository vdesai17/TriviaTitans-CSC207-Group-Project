package trivia.use_case.select_quiza;

import trivia.entity.Question;
import java.util.List;

/**
 * FIXED: Now properly follows Clean Architecture with boundaries and data objects.
 * 
 * Input: SelectQuizInputData (categoryId, difficulty, amount)
 * Processing: Fetches questions from API via data access interface
 * Output: SelectQuizOutputData (list of questions) → Presenter
 * 
 * The interactor NEVER directly manipulates data or calls UI.
 * All communication flows through strict input/output boundaries.
 */
public class SelectQuizInteractor implements SelectQuizInputBoundary {

    private final SelectQuizAPIDataAccessInterface apiDataAccess;
    private final SelectQuizOutputBoundary presenter;

    public SelectQuizInteractor(SelectQuizAPIDataAccessInterface apiDataAccess,
                                SelectQuizOutputBoundary presenter) {
        this.apiDataAccess = apiDataAccess;
        this.presenter = presenter;
    }

    /**
     * Main use case execution - follows input/output boundary pattern
     * 
     * @param inputData Contains: categoryId, difficulty, amount
     */
    @Override
    public void execute(SelectQuizInputData inputData) {
        try {
            // Validate input
            if (inputData == null) {
                presenter.presentFailure("Invalid input data.");
                return;
            }

            String categoryId = inputData.getCategoryId();
            String difficulty = inputData.getDifficulty();
            int amount = inputData.getAmount();

            if (categoryId == null || categoryId.isEmpty()) {
                presenter.presentFailure("Category ID cannot be empty.");
                return;
            }

            if (difficulty == null || difficulty.isEmpty()) {
                presenter.presentFailure("Difficulty cannot be empty.");
                return;
            }

            if (amount <= 0) {
                presenter.presentFailure("Amount must be positive.");
                return;
            }

            // Fetch questions from data access layer (API)
            List<Question> questions = apiDataAccess.fetchQuestions(categoryId, difficulty, amount);

            // Validate result
            if (questions == null || questions.isEmpty()) {
                presenter.presentFailure("No questions found. Try another combination.");
                return;
            }

            // Send successful result to presenter
            SelectQuizOutputData outputData = new SelectQuizOutputData(questions);
            presenter.presentSuccess(outputData);

        } catch (Exception e) {
            presenter.presentFailure("Failed to load questions: " + e.getMessage());
        }
    }
}