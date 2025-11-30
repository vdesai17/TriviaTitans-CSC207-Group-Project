package trivia.use_case.register_player;

import trivia.entity.Player;

/**
 * FIXED: Now depends on interface instead of concrete DAO.
 */
public class RegisterPlayerInteractor {
    private final RegisterPlayerDataAccessInterface playerDAO;  // ✅ FIXED: Now interface

    public RegisterPlayerInteractor(RegisterPlayerDataAccessInterface playerDAO) {
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