package trivia.interface_adapter.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import trivia.entity.Player;
import trivia.entity.Quiz;
import trivia.entity.QuizAttempt;
import trivia.use_case.complete_quiz.QuizAttemptDataAccessInterface;
import trivia.use_case.generate_from_wrong.GenerateFromWrongDataAccessInterface;
import trivia.use_case.generate_from_wrong.WrongQuestionRecord;
import trivia.use_case.review_quiz.ReviewQuizAttemptDataAccessInterface;
import trivia.use_case.review_quiz.ReviewQuizQuizDataAccessInterface;
import trivia.use_case.login.LoginDataAccessInterface;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Player DAO that now also implements Use Case 3 (Review Quiz) and Use Case 6 interfaces.
 * 
 * Supports basic login authentication and multiple players saved in one file.
 */
public class PlayerDataAccessObject implements 
        GenerateFromWrongDataAccessInterface,
        ReviewQuizAttemptDataAccessInterface,
        ReviewQuizQuizDataAccessInterface,
        LoginDataAccessInterface,
        QuizAttemptDataAccessInterface {

    private static final String FILE_PATH = "data/player.json";
    private final Gson gson;

    public PlayerDataAccessObject() {
        // Configure Gson with proper adapters for LocalDateTime and other complex types
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    /**
     * Saves a Player object to player.json.
     * Supports multiple players — appends or updates existing one.
     */
    public void savePlayer(Player player) {
        List<Player> players = loadAllPlayers();

        // Check if player already exists (update instead of duplicate)
        boolean updated = false;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPlayerName().equalsIgnoreCase(player.getPlayerName())) {
                players.set(i, player);
                updated = true;
                break;
            }
        }

        if (!updated) players.add(player);

        try {
            // Ensure the data directory exists
            File file = new File(FILE_PATH);
            file.getParentFile().mkdirs();
            
            Writer writer = new FileWriter(FILE_PATH);
            gson.toJson(players, writer);
            writer.close();
            System.out.println("Saved player: " + player.getPlayerName());
        } catch (IOException e) {
            System.err.println("Failed to save player: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Attempts to load a player by name.
     */
    public Player loadPlayer(String name) {
        List<Player> players = loadAllPlayers();
        for (Player p : players) {
            if (p.getPlayerName().equalsIgnoreCase(name)) {
                System.out.println("Loaded player: " + p.getPlayerName());
                return p;
            }
        }
        System.out.println("Player not found: " + name);
        return null;
    }

    /**
     * Loads all saved players from JSON file.
     */
    private List<Player> loadAllPlayers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("Player file does not exist yet: " + FILE_PATH);
            return new ArrayList<>();
        }

        try {
            Reader reader = new FileReader(file);
            Type listType = new TypeToken<List<Player>>() {}.getType();
            List<Player> players = gson.fromJson(reader, listType);
            reader.close();
            
            if (players == null) {
                System.out.println("No players found in file");
                return new ArrayList<>();
            }
            
            System.out.println("Loaded " + players.size() + " players from file");
            return players;
        } catch (IOException e) {
            System.err.println("Failed to load players: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Failed to parse player JSON: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Validates player login credentials.
     */
    public Player validateLogin(String name, String password) {
        System.out.println("Attempting login for: " + name);
        Player p = loadPlayer(name);
        
        if (p == null) {
            System.out.println("Login failed - player not found: " + name);
            return null;
        }
        
        if (p.verifyPassword(password)) {
            System.out.println("Login successful for: " + name);
            return p;
        }
        
        System.out.println("Login failed - incorrect password for: " + name);
        return null;
    }

    @Override
    public void saveAttempt(QuizAttempt attempt) {
        if (attempt == null || attempt.getUserName() == null) {
            System.err.println("[UC5] Cannot save attempt - missing player information.");
            return;
        }

        List<Player> players = loadAllPlayers();
        boolean found = false;

        for (Player player : players) {
            if (attempt.getUserName().equalsIgnoreCase(player.getPlayerName())) {
                player.getPastAttempts().add(attempt);
                found = true;
                break;
            }
        }

        if (!found) {
            System.err.println("[UC5] Player not found while saving attempt: " + attempt.getUserName());
            return;
        }

        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(players, writer);
        } catch (IOException e) {
            System.err.println("[UC5] Failed to save attempt: " + e.getMessage());
        }
    }

    //  UC3: Review Quiz Attempt Interface  ↓↓↓↓↓↓↓↓↓↓↓

    @Override
    public List<QuizAttempt> getAttemptsForPlayer(String playerName) {
        Player player = loadPlayer(playerName);
        if (player != null) {
            return player.getPastAttempts();
        }
        return new ArrayList<>();
    }

    @Override
    public Optional<QuizAttempt> getAttemptById(String attemptId) {
        List<Player> players = loadAllPlayers();
        for (Player player : players) {
            for (QuizAttempt attempt : player.getPastAttempts()) {
                if (attempt.getAttemptId().equals(attemptId)) {
                    return Optional.of(attempt);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void updateAttempt(QuizAttempt attempt) {
        List<Player> players = loadAllPlayers();
        
        for (Player player : players) {
            List<QuizAttempt> attempts = player.getPastAttempts();
            for (int i = 0; i < attempts.size(); i++) {
                if (attempts.get(i).getAttemptId().equals(attempt.getAttemptId())) {
                    attempts.set(i, attempt);
                    savePlayer(player);  // Save the updated player
                    System.out.println("[UC3] Updated attempt: " + attempt.getAttemptId());
                    return;
                }
            }
        }
        
        System.err.println("[UC3] Attempt not found: " + attempt.getAttemptId());
    }

    @Override
    public Quiz getQuizById(String quizId) {
        // This is a simplified implementation. In a real app, you'd have a quiz repository.
        // For now, we'll search through all attempts to find quizzes
        List<Player> players = loadAllPlayers();
        
        for (Player player : players) {
            for (QuizAttempt attempt : player.getPastAttempts()) {
                Quiz quiz = attempt.getQuiz();
                if (quiz != null && quiz.getId().equals(quizId)) {
                    return quiz;
                }
            }
        }
        
        System.err.println("[UC3] Quiz not found: " + quizId);
        return null;  // Or throw an exception
    }

    // ========================================================================
    //  ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓  UC6: Generate From Wrong Questions  ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // ========================================================================

    @Override
    public List<WrongQuestionRecord> getWrongQuestionsForPlayer(String playerName) {
        System.out.println("[UC6] Returning empty wrong-question list for: " + playerName);
        return new ArrayList<>();
    }

    @Override
    public String createQuizFromWrongQuestions(String playerName, List<WrongQuestionRecord> questions) {
        String quizId = "practice-" + playerName + "-" + System.currentTimeMillis();
        System.out.println("[UC6] Creating new practice quiz: " + quizId +
                " (with " + questions.size() + " wrong questions)");
        return quizId;
    }
}
