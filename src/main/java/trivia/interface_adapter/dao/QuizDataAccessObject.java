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
import trivia.use_case.load_quiz.LoadQuizDataAccessInterface;
import trivia.use_case.create_quiz.CreateQuizDataAccessInterface;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QuizDataAccessObject
        implements ReviewQuizAttemptDataAccessInterface,
        ReviewQuizQuizDataAccessInterface,
        GenerateFromWrongDataAccessInterface,
        QuizAttemptDataAccessInterface,
        LoadQuizDataAccessInterface,
        CreateQuizDataAccessInterface {

    private static final String FILE_PATH = "data/custom_quizzes.json";
    private static final String ATTEMPT_FILE_PATH = "data/quiz_attempts.json";

    private static final List<Quiz> quizzes = new ArrayList<>();
    private static final List<QuizAttempt> attempts = new ArrayList<>();
    private final Gson gson = new Gson();

    public QuizDataAccessObject() {
        if (quizzes.isEmpty()) {
            quizzes.addAll(loadQuizzesFromFile());
        }
        if (attempts.isEmpty()) {
            attempts.addAll(loadAttemptsFromFile());
        }
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

    // ===== CreateQuizDataAccessInterface methods =====
    @Override
    public boolean existsById(String quizId) {
        for (Quiz quiz : quizzes) {
            if (quiz.getId().equals(quizId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void save(Quiz quiz) {
        saveQuiz(quiz);  // Reuse your existing method
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

    private List<QuizAttempt> loadAttemptsFromFile() {
        File file = new File(ATTEMPT_FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<QuizAttempt>>() {}.getType();
            List<QuizAttempt> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Failed to load attempts, starting with empty list: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveAttemptsToFile() {
        try (Writer writer = new FileWriter(ATTEMPT_FILE_PATH)) {
            gson.toJson(attempts, writer);
        } catch (Exception e) {
            System.err.println("Failed to save attempts: " + e.getMessage());
        }
    }

    // ==== Everything below unchanged ====
    @Override
    public void saveAttempt(QuizAttempt attempt) {
        attempts.add(attempt);
        saveAttemptsToFile();
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
        List<QuizAttempt> result = new ArrayList<>();

        for (QuizAttempt attempt : attempts) {
            if (attempt == null) {
                continue;
            }

            boolean matchPlayer = false;

            if (attempt.getUserName() != null) {
                matchPlayer = playerName.equals(attempt.getUserName());
            }
            else if (attempt.getQuiz() != null
                    && attempt.getQuiz().getCreatorName() != null) {
                matchPlayer = playerName.equals(attempt.getQuiz().getCreatorName());
            }

            if (matchPlayer) {
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
                saveAttemptsToFile();
                return;
            }
        }
        attempts.add(updatedAttempt);
        saveAttemptsToFile();
    }

    @Override
    public List<WrongQuestionRecord> getWrongQuestionsForPlayer(String playerName) {
        List<WrongQuestionRecord> result = new ArrayList<>();

        PlayerDataAccessObject playerDAO = new PlayerDataAccessObject();
        List<QuizAttempt> playerAttempts = playerDAO.getAttemptsForPlayer(playerName);

        for (QuizAttempt attempt : playerAttempts) {
            if (attempt == null) {
                continue;
            }

            Quiz quiz = attempt.getQuiz();
            if (quiz == null) {
                continue;
            }

            List<Question> questionList = quiz.getQuestions();
            if (questionList == null || questionList.isEmpty()) {
                continue;
            }

            List<String> userAnswers = attempt.getUserAnswers();
            List<Integer> selectedIndices = attempt.getSelectedOptionIndices();

            int size = questionList.size();

            for (int i = 0; i < size; i++) {
                Question q = questionList.get(i);
                if (q == null) {
                    continue;
                }

                String correct = q.getCorrectAnswer();
                if (correct == null) {
                    continue;
                }

                String userAns = null;

                if (userAnswers != null && i < userAnswers.size()) {
                    userAns = userAnswers.get(i);
                }
                else if (selectedIndices != null && i < selectedIndices.size()) {
                    Integer idx = selectedIndices.get(i);
                    List<String> opts = q.getOptions();
                    if (idx != null && idx >= 0 && opts != null && idx < opts.size()) {
                        userAns = opts.get(idx);
                    }
                }

                if (userAns == null) {
                    continue;
                }

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