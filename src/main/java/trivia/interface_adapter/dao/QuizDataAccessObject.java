package trivia.interface_adapter.dao;

import trivia.entity.Question;
import trivia.entity.Quiz;
import trivia.entity.QuizAttempt;
import trivia.use_case.generate_from_wrong.GenerateFromWrongDataAccessInterface;
import trivia.use_case.generate_from_wrong.WrongQuestionRecord;
import trivia.use_case.review_quiz.ReviewQuizAttemptDataAccessInterface;
import trivia.use_case.review_quiz.ReviewQuizQuizDataAccessInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Simple in-memory QuizDataAccessObject that also supports the review_quiz use case
 * and UC6 (generate quiz from wrong questions).
 *
 * TODO: Replace the in-memory lists with your real file/JSON persistence logic.
 */
public class QuizDataAccessObject
        implements ReviewQuizAttemptDataAccessInterface,
        ReviewQuizQuizDataAccessInterface,
        GenerateFromWrongDataAccessInterface {

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

    /*
    // If you used a constructor with file paths before, you can keep it as well:
    public QuizDataAccessObject(String quizzesPath, String attemptsPath) {
        // TODO: load from the given paths
    }
    */

    // ------------------------------------------------------------------------
    // BASIC QUIZ OPERATIONS (for create/play use cases)
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
        return new ArrayList<>(attempts);
    }

    @Override
    public Optional<QuizAttempt> getAttemptById(String attemptId) {
        return Optional.empty();
    }

    @Override
    public void updateAttempt(QuizAttempt updatedAttempt) {
        for (int i = 0; i < attempts.size(); i++) {
            if (attempts.get(i) == updatedAttempt) {
                attempts.set(i, updatedAttempt);
                // TODO: if you persist attempts to JSON, write them out here
                return;
            }
        }
        // If not found, you might want to add it:
        // attempts.add(updatedAttempt);
    }

    // ========================================================================
    //  UC6: Generate quiz from wrong questions
    // ========================================================================

    @Override
    public List<WrongQuestionRecord> getWrongQuestionsForPlayer(String playerName) {
        List<WrongQuestionRecord> result = new ArrayList<>();

        for (QuizAttempt attempt : attempts) {

            Quiz quiz = attempt.getQuiz();
            if (quiz == null) {
                continue;
            }

            List<Question> questionList = quiz.getQuestions();
            List<String> userAnswers = attempt.getUserAnswers();
            if (questionList == null || userAnswers == null) {
                continue;
            }

            int size = Math.min(questionList.size(), userAnswers.size());
            for (int i = 0; i < size; i++) {
                Question q = questionList.get(i);
                String userAns = userAnswers.get(i);

                String correct = q.getCorrectAnswer();

                if (!correct.equals(userAns)) {
                    WrongQuestionRecord record = new WrongQuestionRecord(
                            quiz.getId(),
                            quiz.getTitle(),
                            q.getQuestionText(),
                            q.getOptions(),
                            correct
                    );
                    result.add(record);
                }
            }
        }

        return result;
    }

    @Override
    public String createQuizFromWrongQuestions(String playerName,
                                               List<WrongQuestionRecord> questions) {
        if (questions == null || questions.isEmpty()) {
            return null;
        }

        String quizId = "practice-" + playerName + "-" + System.currentTimeMillis();

        List<Question> questionEntities = new ArrayList<>();
        for (WrongQuestionRecord record : questions) {
            Question q = new Question(
                    record.getQuestionId(),
                    record.getQuestionText(),
                    record.getOptions(),
                    record.getCorrectAnswer(),
                    "Practice",
                    "mixed"
            );
            questionEntities.add(q);
        }

        Quiz newQuiz = new Quiz(
                quizId,
                "Practice from Wrong Questions",
                "Practice",
                "mixed",
                playerName,
                questionEntities
        );

        saveQuiz(newQuiz);

        return quizId;
    }

    // ------------------------------------------------------------------------
    // PLACEHOLDER: put any other existing methods from your old file here
    // (e.g., methods for loading from LocalFileHandler, player stats, etc.)
    // ------------------------------------------------------------------------
}
