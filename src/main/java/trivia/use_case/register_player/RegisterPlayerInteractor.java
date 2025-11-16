package trivia.use_case.register_player;

import trivia.entity.Player;
import trivia.interface_adapter.dao.PlayerDataAccessObject;

public class RegisterPlayerInteractor {
    private final PlayerDataAccessObject playerDAO;

    public RegisterPlayerInteractor(PlayerDataAccessObject playerDAO) {
        this.playerDAO = playerDAO;
    }

    public Player registerPlayer(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be empty");
        }
        Player player = new Player(name.trim());
        playerDAO.savePlayer(player);
        return player;
    }
}
