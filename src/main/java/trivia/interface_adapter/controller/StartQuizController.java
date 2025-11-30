package trivia.interface_adapter.controller;

import trivia.use_case.start_quiz.StartQuizInputData;
import trivia.use_case.start_quiz.StartQuizInteractor;
import trivia.use_case.start_quiz.StartQuizOutputData;

public class StartQuizController {
    private final StartQuizInteractor interactor;

    public StartQuizController(StartQuizInteractor interactor) {
        this.interactor = interactor;
    }

    public StartQuizOutputData startQuiz(String quizId, String playerName) {
        StartQuizInputData inputData = new StartQuizInputData(quizId, playerName);
        return interactor.execute(inputData);
    }
}