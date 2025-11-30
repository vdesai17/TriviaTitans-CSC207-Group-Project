package trivia.interface_adapter.controller;

import trivia.use_case.load_quiz.*;
import trivia.entity.Quiz;
import trivia.interface_adapter.presenter.LoadQuizPresenter;
import java.util.List;

public class LoadQuizController {
    private final LoadQuizInputBoundary interactor;
    private final LoadQuizPresenter presenter;

    public LoadQuizController(LoadQuizInputBoundary interactor, LoadQuizPresenter presenter) {
        this.interactor = interactor;
        this.presenter = presenter;
    }

    public List<Quiz> loadQuizzesForPlayer(String playerName) {
        LoadQuizInputData inputData = new LoadQuizInputData(playerName);
        interactor.execute(inputData);
        return presenter.getQuizzes();
    }
}
