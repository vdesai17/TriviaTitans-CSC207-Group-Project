package trivia.use_case.generate_from_wrong;

import java.util.List;

/**
 * Data access interface for Use Case 6.
 *
 * Your DAO implementation (for example, QuizDataAccessObject)
 * will implement this interface.
 */
public interface GenerateFromWrongDataAccessInterface {

    /**
     * Return all questions that the given player has previously
     * answered incorrectly.
     *
     * @param playerName name or id of the player.
     * @return list of wrong question records.
     */
    List<WrongQuestionRecord> getWrongQuestionsForPlayer(String playerName);

    /**
     * Create a new quiz from the selected wrong questions and return its id.
     * How this quiz is stored (file / DB / in-memory) is decided by
     * the implementing DAO.
     *
     * @param playerName the player who will take this quiz.
     * @param questions  questions to include in the quiz.
     * @return id / name of the created quiz.
     */
    String createQuizFromWrongQuestions(String playerName,
                                        List<WrongQuestionRecord> questions);
}
