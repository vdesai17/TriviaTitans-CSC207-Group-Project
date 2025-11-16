package trivia.interface_adapter.dao;

import com.google.gson.Gson;
import trivia.entity.Player;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class PlayerDataAccessObject {
    private static final String FILE_PATH = "data/player.json";
    private final Gson gson = new Gson();

    public void savePlayer(Player player) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(player, writer);
            System.out.println("Saved player: " + player.getPlayerName());
        } catch (IOException e) {
            System.err.println("Failed to save player: " + e.getMessage());
        }
    }
}
