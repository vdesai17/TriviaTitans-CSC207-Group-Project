package trivia.use_case.view_profile;

import trivia.entity.Player;
import java.util.List;

public interface ViewProfileDataAccessInterface {
    Player loadPlayer(String playerName);
    List<Player> getAllPlayers();
}