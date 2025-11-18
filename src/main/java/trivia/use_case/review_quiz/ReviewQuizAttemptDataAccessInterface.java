package trivia.use_case.review_quiz;

import trivia.entity.QuizAttempt;

import java.util.List;
import java.util.Optional;

public interface ReviewQuizAttemptDataAccessInterface {

    List<QuizAttempt> getAttemptsForPlayer(String playerName);

    Optional<QuizAttempt> getAttemptById(String attemptId);

    void updateAttempt(QuizAttempt attempt);
}
