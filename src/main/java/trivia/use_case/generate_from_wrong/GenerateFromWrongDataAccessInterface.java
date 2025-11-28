package trivia.use_case.generate_from_wrong;

import java.util.List;

/**
 * Data access interface for Use Case 6:
 * Generate a quiz from the user's past wrong questions.
 *
 * Implemented by QuizDataAccessObject.
 */
public interface GenerateFromWrongDataAccessInterface {

    /**
     * Return all wrong-question records for a given player.
     */
    List<WrongQuestionRecord> getWrongQuestionsForPlayer(String playerName);

    /**
     * Create a new practice quiz (from wrong questions), store it,
     * and return the new quizId.
     */
    String createQuizFromWrongQuestions(String playerName,
                                        List<WrongQuestionRecord> questions);
}
