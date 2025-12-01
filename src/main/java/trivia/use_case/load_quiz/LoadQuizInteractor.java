package trivia.use_case.load_quiz;

import java.util.Collections;
import java.util.List;
import trivia.entity.Quiz;

/**
 * Interactor for the Load Quiz use case.
 */
public class LoadQuizInteractor implements LoadQuizInputBoundary {

    /** The quiz data access interface. */
    private final LoadQuizDataAccessInterface quizDataAccess;

    /** The presenter output boundary. */
    private final LoadQuizOutputBoundary presenter;

    /**
     * Constructs a LoadQuizInteractor.
     *
     * @param quizDataAccessInput the data access interface
     * @param presenterInput the presenter output boundary
     */
    public LoadQuizInteractor(
            final LoadQuizDataAccessInterface quizDataAccessInput,
            final LoadQuizOutputBoundary presenterInput) {

        if (quizDataAccessInput == null) {
            throw new IllegalArgumentException("QuizDataAccess cannot be null");
        }

        if (presenterInput == null) {
            throw new IllegalArgumentException("Presenter cannot be null");
        }

        this.quizDataAccess = quizDataAccessInput;
        this.presenter = presenterInput;
    }

    /**
     * Executes the load quiz use case.
     *
     * @param inputData the input data
     */
    @Override
    public void execute(final LoadQuizInputData inputData) {

        if (inputData == null || inputData.getPlayerName() == null) {
            presenter.present(
                    new LoadQuizResponseModel(Collections.emptyList()));
            return;
        }

        final String playerName = inputData.getPlayerName();
        List<Quiz> quizzes =
                quizDataAccess.getQuizzesByPlayer(playerName);

        if (quizzes == null) {
            quizzes = Collections.emptyList();
        }

        final LoadQuizResponseModel responseModel =
                new LoadQuizResponseModel(quizzes);

        presenter.present(responseModel);
    }
}
