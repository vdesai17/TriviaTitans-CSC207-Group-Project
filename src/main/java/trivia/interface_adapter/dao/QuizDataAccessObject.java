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

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizDataAccessObject
        implements ReviewQuizAttemptDataAccessInterface,
        ReviewQuizQuizDataAccessInterface,
        GenerateFromWrongDataAccessInterface {

    private static final String FILE_PATH = "data/custom_quizzes.json";
    private final List<Quiz> quizzes;
    private final List<QuizAttempt> attempts = new ArrayList<>();
    private final Gson gson = new Gson();

    public QuizDataAccessObject() {
        this.quizzes = loadQuizzesFromFile();
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
        return new ArrayList<>();
    }

    @Override
    public String createQuizFromWrongQuestions(String playerName, List<WrongQuestionRecord> questions) {
        return null;
    }
}
