package trivia.use_case.register_player;

import trivia.entity.Player;
import trivia.interface_adapter.dao.PlayerDataAccessObject;

/**
 * Application business logic for the "Enter Player Information" use case.
 *
 * <p>This interactor is responsible for:
 * <ul>
 *     <li>Validating the raw name/password provided by the UI/controller</li>
 *     <li>Creating a {@link Player} entity</li>
 *     <li>Persisting the {@link Player} using {@link PlayerDataAccessObject}</li>
 * </ul>
 *
 * <p>The public API is kept intentionally small so other layers do not
 * need to change when we improve the internal logic.</p>
 */
public class RegisterPlayerInteractor {

    private final PlayerDataAccessObject playerDAO;

    /**
     * Create a new interactor that will use the given data access object
     * to persist players.
     *
     * @param playerDAO the DAO used to save {@link Player} instances
     * @throws IllegalArgumentException if {@code playerDAO} is {@code null}
     */
    public RegisterPlayerInteractor(PlayerDataAccessObject playerDAO) {
        if (playerDAO == null) {
            throw new IllegalArgumentException("playerDAO must not be null");
        }
        this.playerDAO = playerDAO;
    }

    /**
     * Legacy helper kept for backward compatibility with older callers that
     * only pass a name. It simply delegates to the two-argument version
     * with an empty password.
     *
     * <p>If you are writing new code, prefer calling
     * {@link #registerPlayer(String, String)} instead.</p>
     */
    public Player registerPlayer(String name) {
        return registerPlayer(name, "");
    }

    /**
     * Register a new player with the given name and password.
     *
     * <p>The input is first sanitised (trimming whitespace), then validated.
     * If validation fails an {@link IllegalArgumentException} is thrown
     * with a user-friendly message that the UI layer can display
     * directly.</p>
     *
     * @param name     raw name string entered by the user
     * @param password raw password string entered by the user
     * @return the newly created {@link Player}
     * @throws IllegalArgumentException if the name or password are invalid
     */
    public Player registerPlayer(String name, String password) {
        // 1. Normalise input from the UI
        String sanitizedName = sanitize(name);
        String sanitizedPassword = sanitize(password);

        // 2. Validate business rules
        validateNameAndPassword(sanitizedName, sanitizedPassword);

        // 3. Create the Player entity
        Player player = new Player(sanitizedName, sanitizedPassword);

        // 4. Persist using DAO
        playerDAO.savePlayer(player);

        return player;
    }

    /**
     * Apply simple sanitisation to user input.
     * Currently this only trims leading/trailing whitespace.
     */
    private String sanitize(String raw) {
        return raw == null ? null : raw.trim();
    }

    /**
     * Centralised validation for the registration use case.
     *
     * @throws IllegalArgumentException if any rule is violated
     */
    private void validateNameAndPassword(String name, String password) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }


    }
}

