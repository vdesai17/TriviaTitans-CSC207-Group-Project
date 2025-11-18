package trivia.interface_adapter.controller;

import trivia.entity.Question;
import trivia.use_case.select_quiz.SelectQuizInteractor;
import java.util.List;

public class SelectQuizController {
    private final SelectQuizInteractor interactor;

    public SelectQuizController(SelectQuizInteractor interactor) {
        this.interactor = interactor;
    }

    public List<Question> getQuestions(String categoryId, String difficulty, int amount) {
        return interactor.loadQuestions(categoryId, difficulty, amount);
    }
}
