package trivia.use_case.select_quiz;

import trivia.entity.Question;
import trivia.interface_adapter.api.APIManager;
import java.util.List;

public class SelectQuizInteractor {
    private final APIManager apiManager;

    public SelectQuizInteractor(APIManager apiManager) {
        this.apiManager = apiManager;
    }

    public List<Question> loadQuestions(String categoryId, String difficulty, int amount) {
        return apiManager.fetchQuestions(categoryId, difficulty, amount);
    }
}
