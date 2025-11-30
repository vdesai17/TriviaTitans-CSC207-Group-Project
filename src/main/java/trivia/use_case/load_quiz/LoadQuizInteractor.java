package trivia.use_case.load_quiz;

import trivia.entity.Quiz;
import trivia.interface_adapter.dao.QuizDataAccessObject;
import trivia.interface_adapter.presenter.LoadQuizPresenter;

import java.util.Collections;
import java.util.List;

/**
 * LoadQuizInteractor — Use case interactor responsible for retrieving
 * all quizzes created by a specific player and sending them to the presenter.
 *
 * Follows Clean Architecture:
 * - Depends only on abstractions (Presenter and DAO)
 * - Handles no UI logic
 */
public class LoadQuizInteractor implements LoadQuizInputBoundary {

    private final QuizDataAccessObject quizDataAccessObject;
    private final LoadQuizPresenter presenter;

    public LoadQuizInteractor(QuizDataAccessObject quizDataAccessObject, LoadQuizPresenter presenter) {
        if (quizDataAccessObject == null) {
            throw new IllegalArgumentException("QuizDataAccessObject cannot be null");
        }
        if (presenter == null) {
            throw new IllegalArgumentException("Presenter cannot be null");
        }
        this.quizDataAccessObject = quizDataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute(LoadQuizInputData inputData) {
        if (inputData == null || inputData.getPlayerName() == null) {
            presenter.present(new LoadQuizResponseModel(Collections.emptyList()));
            return;
        }

        String playerName = inputData.getPlayerName();
        List<Quiz> quizzes = quizDataAccessObject.getQuizzesByPlayer(playerName);

        if (quizzes == null) {
            quizzes = Collections.emptyList();
        }

        LoadQuizResponseModel responseModel = new LoadQuizResponseModel(quizzes);
        presenter.present(responseModel);
    }
}
