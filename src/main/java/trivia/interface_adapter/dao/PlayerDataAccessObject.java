package trivia.interface_adapter.dao;

import com.google.gson.Gson;
import trivia.entity.Player;
import trivia.use_case.generate_from_wrong.GenerateFromWrongDataAccessInterface;
import trivia.use_case.generate_from_wrong.WrongQuestionRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Player DAO + temporary implementation for UC6.
 *
 * Currently stores only player info (to player.json)
 * and returns empty data for wrong questions (so UC6 does not crash).
 */
public class PlayerDataAccessObject implements GenerateFromWrongDataAccessInterface {

    private static final String FILE_PATH = "data/player.json";
    private final Gson gson = new Gson();

    /**
     * Saves a Player object to player.json
     */
    public void savePlayer(Player player) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(player, writer);
            System.out.println("Saved player: " + player.getPlayerName());
        } catch (IOException e) {
            System.err.println("Failed to save player: " + e.getMessage());
        }
    }

    // ========================================================================
    //  ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓  UC6: Generate From Wrong Questions  ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // ========================================================================

    @Override
    public List<WrongQuestionRecord> getWrongQuestionsForPlayer(String playerName) {
        // TODO: Replace with real logic when your team implements wrong-question logging
        // For now, return empty list so the UC6 flow works correctly.
        System.out.println("[UC6] Returning empty wrong-question list for: " + playerName);
        return new ArrayList<>();
    }

    @Override
    public String createQuizFromWrongQuestions(String playerName,
                                               List<WrongQuestionRecord> questions) {
        // TODO: Replace with real quiz creation logic later
        String quizId = "practice-" + playerName + "-" + System.currentTimeMillis();
        System.out.println("[UC6] Creating new practice quiz: " + quizId +
                " (with " + questions.size() + " wrong questions)");
        return quizId;
    }
}
