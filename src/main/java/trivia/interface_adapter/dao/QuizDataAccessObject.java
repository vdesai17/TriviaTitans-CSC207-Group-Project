package trivia.interface_adapter.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import trivia.entity.Question;
import trivia.entity.Quiz;
import trivia.entity.QuizAttempt;
import trivia.use_case.generate_from_wrong.GenerateFromWrongDataAccessInterface;
import trivia.use_case.generate_from_wrong.WrongQuestionRecord;
import trivia.use_case.review_quiz.ReviewQuizAttemptDataAccessInterface;
import trivia.use_case.review_quiz.ReviewQuizQuizDataAccessInterface;
import trivia.use_case.complete_quiz.QuizAttemptDataAccessInterface;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizDataAccessObject
        implements ReviewQuizAttemptDataAccessInterface,
        ReviewQuizQuizDataAccessInterface,
        GenerateFromWrongDataAccessInterface,
        QuizAttemptDataAccessInterface {

    private static final String FILE_PATH = "data/custom_quizzes.json";
    private static final List<Quiz> quizzes = new ArrayList<>();
    private static final List<QuizAttempt> attempts = new ArrayList<>();
    private final Gson gson = new Gson();

    public QuizDataAccessObject() {
        List<Quiz> loaded = loadQuizzesFromFile();
        quizzes.clear();
        quizzes.addAll(loaded);
    }

    /** Save or update a quiz, persist to JSON */
    public void saveQuiz(Quiz quiz) {
        for (int i = 0; i < quizzes.size(); i++) {
            if (quizzes.get(i).getId().equals(quiz.getId())) {
                quizzes.set(i, quiz);
                saveQuizzesToFile();
                return;
            }
        }
        quizzes.add(quiz);
        saveQuizzesToFile();
    }

    /** Return all quizzes */
    public List<Quiz> getAllQuizzes() {
        return new ArrayList<>(quizzes);
    }

    /** Get all quizzes created by a specific player */
    public List<Quiz> getQuizzesByPlayer(String playerName) {
        List<Quiz> playerQuizzes = new ArrayList<>();
        for (Quiz q : quizzes) {
            if (q.getCreatorName().equalsIgnoreCase(playerName)) {
                playerQuizzes.add(q);
            }
        }
        return playerQuizzes;
    }

    // ===== JSON Persistence =====
    private List<Quiz> loadQuizzesFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Quiz>>() {}.getType();
            List<Quiz> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Failed to load quizzes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveQuizzesToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(quizzes, writer);
        } catch (IOException e) {
            System.err.println("Failed to save quizzes: " + e.getMessage());
        }
    }

    // ==== Everything below unchanged ====
    @Override
    public void saveAttempt(QuizAttempt attempt) {
        attempts.add(attempt);
    }

    @Override
    public Quiz getQuizById(String quizId) {
        for (Quiz quiz : quizzes) {
            if (quiz.getId().equals(quizId)) {
                return quiz;
            }
        }
        return null;
    }

    @Override
    public List<QuizAttempt> getAttemptsForPlayer(String playerName) {
        return new ArrayList<>(attempts);
    }

    @Override
    public Optional<QuizAttempt> getAttemptById(String attemptId) {
        return Optional.empty();
    }

    @Override
    public void updateAttempt(QuizAttempt updatedAttempt) {}

    @Override
    public List<WrongQuestionRecord> getWrongQuestionsForPlayer(String playerName) {
        List<WrongQuestionRecord> result = new ArrayList<>();

        for (QuizAttempt attempt : attempts) {
            if (!playerName.equals(attempt.getUserName())) {
                continue;
            }

            Quiz quiz = attempt.getQuiz();
            if (quiz == null) {
                continue;
            }

            List<trivia.entity.Question> questionList = quiz.getQuestions();
            List<String> userAnswers = attempt.getUserAnswers();
            if (questionList == null || userAnswers == null) {
                continue;
            }

            int size = Math.min(questionList.size(), userAnswers.size());
            for (int i = 0; i < size; i++) {
                trivia.entity.Question q = questionList.get(i);
                String userAns = userAnswers.get(i);
                String correct = q.getCorrectAnswer();

                if (!correct.equals(userAns)) {
                    WrongQuestionRecord record = new WrongQuestionRecord(
                            quiz.getId(),
                            "Practice from Wrong Questions",
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

        List<trivia.entity.Question> questionEntities = new ArrayList<>();
        for (WrongQuestionRecord record : questions) {
            trivia.entity.Question q = new trivia.entity.Question(
                    "",
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
}
