package trivia.use_case.complete_quiz;

import trivia.entity.QuizAttempt;

/**
 * Data access interface for saving quiz attempts (used by UC6 + complete_quiz).
 */
public interface QuizAttemptDataAccessInterface {

    /**
     * Save a completed quiz attempt.
     */
    void saveAttempt(QuizAttempt attempt);
}
