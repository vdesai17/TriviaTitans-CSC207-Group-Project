package trivia.interface_adapter.controller;

import trivia.use_case.load_quiz.LoadQuizInputBoundary;
import trivia.use_case.load_quiz.LoadQuizInputData;

public class LoadQuizController {
    private final LoadQuizInputBoundary interactor;

    public LoadQuizController(LoadQuizInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void loadQuizzes(String playerName) {
        LoadQuizInputData inputData = new LoadQuizInputData(playerName);
        interactor.execute(inputData);
    }
}