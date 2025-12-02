package trivia.use_case.select_quiza;

import trivia.entity.Question;
import java.util.List;

public interface SelectQuizAPIDataAccessInterface {
    List<Question> fetchQuestions(String categoryId, String difficulty, int amount);
}