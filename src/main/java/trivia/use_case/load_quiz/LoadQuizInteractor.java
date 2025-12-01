package trivia.use_case.load_quiz;

import trivia.entity.Quiz;
import java.util.Collections;
import java.util.List;


public class LoadQuizInteractor implements LoadQuizInputBoundary {

    private final LoadQuizDataAccessInterface quizDataAccess;
    private final LoadQuizOutputBoundary presenter;

    public LoadQuizInteractor(LoadQuizDataAccessInterface
                                      quizDataAccess,
                              LoadQuizOutputBoundary presenter) {

        if (quizDataAccess == null) {
            throw new IllegalArgumentException("QuizDataAccess cannot be null");
        }
        if (presenter == null) {
            throw new IllegalArgumentException("Presenter cannot be null");
        }
        this.quizDataAccess = quizDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(LoadQuizInputData inputData) {
        if (inputData == null || inputData.getPlayerName() == null) {
            presenter.present(new LoadQuizResponseModel(Collections.emptyList()));
            return;
        }

        String playerName = inputData.getPlayerName();
        List<Quiz> quizzes = quizDataAccess.getQuizzesByPlayer(playerName);

        if (quizzes == null) {
            quizzes = Collections.emptyList();
        }

        LoadQuizResponseModel responseModel = new LoadQuizResponseModel(quizzes);
        presenter.present(responseModel);
    }
}








