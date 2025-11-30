package trivia.use_case.select_quiz;

import trivia.entity.Question;
import java.util.List;

/**
 * FIXED: Now follows proper Clean Architecture with boundaries and data objects.
 */
public class SelectQuizInteractor implements SelectQuizInputBoundary {

    private final SelectQuizAPIDataAccessInterface apiDataAccess;
    private final SelectQuizOutputBoundary presenter;

    public SelectQuizInteractor(SelectQuizAPIDataAccessInterface apiDataAccess,
                                SelectQuizOutputBoundary presenter) {
        this.apiDataAccess = apiDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(SelectQuizInputData inputData) {
        try {
            List<Question> questions = apiDataAccess.fetchQuestions(
                    inputData.getCategoryId(),
                    inputData.getDifficulty(),
                    inputData.getAmount()
            );

            if (questions == null || questions.isEmpty()) {
                presenter.presentFailure("No questions found. Try another combination.");
                return;
            }

            SelectQuizOutputData outputData = new SelectQuizOutputData(questions);
            presenter.presentSuccess(outputData);

        } catch (Exception e) {
            presenter.presentFailure("Failed to load questions: " + e.getMessage());
        }
    }
}