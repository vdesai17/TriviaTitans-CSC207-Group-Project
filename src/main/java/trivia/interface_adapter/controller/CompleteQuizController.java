package trivia.interface_adapter.controller;

import trivia.use_case.complete_quiz.CompleteQuizInputBoundary;
import trivia.use_case.complete_quiz.CompleteQuizInputData;
import trivia.entity.Question;

import java.util.List;

public class CompleteQuizController {

    private final CompleteQuizInputBoundary interactor;

    public CompleteQuizController(CompleteQuizInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String playerName,
                        List<Question> questions,
                        List<String> userAnswers) {

        CompleteQuizInputData inputData =
                new CompleteQuizInputData(playerName, questions, userAnswers);

        interactor.execute(inputData);
    }
}
