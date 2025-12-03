package trivia.use_case.select_quiz;

import trivia.entity.Question;
import java.util.List;

/**
 * Data access boundary for the "Select Quiz" use case.
 *
 * <p>This interface defines how the interactor talks to the external
 * question source (e.g., a REST API).
 *
 * <p>According to Clean Architecture, this interface lives inside the
 * use case layer and is implemented in the outer layers (e.g. APIManager).
 * That way, the interactor does not depend on any concrete API client.
 */

public interface SelectQuizAPIDataAccessInterface {

    /**
     * Fetches a list of questions from an external source based on the
     * chosen category, difficulty, and desired number of questions.
     *
     * @param categoryId the ID or code representing the quiz category
     *                   (e.g., "9" for General Knowledge)
     * @param difficulty the difficulty level (e.g., "easy", "medium", "hard")
     * @param amount     how many questions to fetch
     * @return a list of {@link Question} entities that match the request
     */

    List<Question> fetchQuestions(String categoryId, String difficulty, int amount);
}