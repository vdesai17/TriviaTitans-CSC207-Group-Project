package trivia.interface_adapter.controller;

import trivia.use_case.select_quiz.SelectQuizInputBoundary;
import trivia.use_case.select_quiz.SelectQuizInputData;

/**
 * FIXED: Now uses proper input boundary and input data.
 */
public class SelectQuizController {
    private final SelectQuizInputBoundary interactor;

    public SelectQuizController(SelectQuizInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String categoryId, String difficulty, int amount) {
        SelectQuizInputData inputData = new SelectQuizInputData(categoryId, difficulty, amount);
        interactor.execute(inputData);
    }
}