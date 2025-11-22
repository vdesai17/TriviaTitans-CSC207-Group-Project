package trivia.interface_adapter.dao;

import trivia.entity.Quiz;
import trivia.entity.QuizAttempt;
import trivia.use_case.review_quiz.ReviewQuizAttemptDataAccessInterface;
import trivia.use_case.review_quiz.ReviewQuizQuizDataAccessInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Simple in-memory QuizDataAccessObject that also supports the review_quiz use case.
 * TODO: Replace the in-memory lists with your real file/JSON persistence logic.
 */
public class QuizDataAccessObject
        implements ReviewQuizAttemptDataAccessInterface,
                   ReviewQuizQuizDataAccessInterface {

    // ------------------------------------------------------------------------
    // Fields (for now, simple in-memory lists so everything compiles)
    // ------------------------------------------------------------------------
    private final List<Quiz> quizzes = new ArrayList<>();
    private final List<QuizAttempt> attempts = new ArrayList<>();

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    public QuizDataAccessObject() {
        // TODO: if you load quizzes/attempts from JSON files, do it here
        // e.g. load from "data/quizzes.json" and "data/attempts.json"
    }

    // If you used a constructor with file paths before, you can keep it as well:
    /*
    public QuizDataAccessObject(String quizzesPath, String attemptsPath) {
        // TODO: load from the given paths
    }
    */

    // ------------------------------------------------------------------------
    // BASIC QUIZ OPERATIONS (for create/play use cases)
    // You can adapt these names to match what your interactors expect.
    // ------------------------------------------------------------------------

    /** Save or update a quiz in memory. */
    public void saveQuiz(Quiz quiz) {
        // remove existing with same id if any, then add
        for (int i = 0; i < quizzes.size(); i++) {
            if (quizzes.get(i).getId().equals(quiz.getId())) {
                quizzes.set(i, quiz);
                return;
            }
        }
        quizzes.add(quiz);
    }

    /** Return all quizzes. */
    public List<Quiz> getAllQuizzes() {
        return new ArrayList<>(quizzes);
    }

    /** Save an attempt (used when a player finishes a quiz). */
    public void saveAttempt(QuizAttempt attempt) {
        attempts.add(attempt);
        // TODO: if you persist attempts to JSON, write them out here
    }

    // ------------------------------------------------------------------------
    // Methods required by ReviewQuizQuizDataAccessInterface
    // ------------------------------------------------------------------------

    @Override
    public Quiz getQuizById(String quizId) {
        for (Quiz quiz : quizzes) {
            // adjust getId() if your Quiz class uses another name
            if (quiz.getId().equals(quizId)) {
                return quiz;
            }
        }
        return null; // or throw if you prefer
    }

    // ------------------------------------------------------------------------
    // Methods required by ReviewQuizAttemptDataAccessInterface
    // ------------------------------------------------------------------------

    @Override
    public List<QuizAttempt> getAttemptsForPlayer(String playerName) {
        List<QuizAttempt> result = new ArrayList<>();
        for (QuizAttempt attempt : attempts) {
            if (playerName.equals(attempt.getPlayerName())) {
                result.add(attempt);
            }
        }
        return result;
    }

    @Override
    public Optional<QuizAttempt> getAttemptById(String attemptId) {
        for (QuizAttempt attempt : attempts) {
            if (attempt.getAttemptId().equals(attemptId)) {
                return Optional.of(attempt);
            }
        }
        return Optional.empty();
    }

    @Override
    public void updateAttempt(QuizAttempt updatedAttempt) {
        for (int i = 0; i < attempts.size(); i++) {
            if (attempts.get(i).getAttemptId().equals(updatedAttempt.getAttemptId())) {
                attempts.set(i, updatedAttempt);
                // TODO: if you persist attempts to JSON, write them out here
                return;
            }
        }
        // If not found, you might want to add it:
        // attempts.add(updatedAttempt);
    }

    // ------------------------------------------------------------------------
    // PLACEHOLDER: put any other existing methods from your old file here
    // (e.g., methods for loading from LocalFileHandler, player stats, etc.)
    // ------------------------------------------------------------------------
}
