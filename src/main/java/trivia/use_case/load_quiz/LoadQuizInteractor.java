package trivia.use_case.load_quiz;

import trivia.entity.Quiz;
import trivia.interface_adapter.dao.QuizDataAccessObject;
import trivia.interface_adapter.presenter.LoadQuizPresenter;
import java.util.List;

public class LoadQuizInteractor implements LoadQuizInputBoundary {

    private final QuizDataAccessObject quizDataAccessObject;
    private final LoadQuizPresenter presenter;

    public LoadQuizInteractor(QuizDataAccessObject quizDataAccessObject, LoadQuizPresenter presenter) {
        this.quizDataAccessObject = quizDataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute(LoadQuizInputData inputData) {
        String playerName = inputData.getPlayerName();
        List<Quiz> quizzes = quizDataAccessObject.getQuizzesByPlayer(playerName);

        LoadQuizResponseModel responseModel = new LoadQuizResponseModel(quizzes);
        presenter.present(responseModel);
    }
}
