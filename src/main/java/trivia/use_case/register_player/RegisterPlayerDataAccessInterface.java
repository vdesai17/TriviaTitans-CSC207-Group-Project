package trivia.use_case.register_player;

import trivia.entity.Player;

public interface RegisterPlayerDataAccessInterface {
    void savePlayer(Player player);
    Player loadPlayer(String playerName);
}