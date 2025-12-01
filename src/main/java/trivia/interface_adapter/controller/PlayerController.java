package trivia.interface_adapter.controller;

import trivia.entity.Player;
import trivia.use_case.register_player.RegisterPlayerInteractor;

/**
 * Controller for the "Enter Player Information" use case.
 *
 * <p>This class sits between the UI (Swing screens) and the
 * {@link RegisterPlayerInteractor}. It converts raw UI data into
 * the types expected by the use case and shields the UI from
 * any future changes to the interactor.</p>
 */
public class PlayerController {

    private final RegisterPlayerInteractor interactor;

    public PlayerController(RegisterPlayerInteractor interactor) {
        this.interactor = interactor;
    }

    /**
     * Legacy helper kept for older code paths that only supplied a name.
     * Prefer calling {@link #createPlayer(String, String)} in new code.
     */
    public Player createPlayer(String name) {
        return interactor.registerPlayer(name);
    }

    /**
     * Create (register) a new player with the given name and password.
     *
     * <p>The controller does not perform validation itself; it simply
     * forwards the data to the use case interactor. Any
     * {@link IllegalArgumentException} thrown by the interactor can be
     * caught by the UI layer to show an error message.</p>
     *
     * @param name     player name entered in the UI
     * @param password password entered in the UI
     * @return the newly created {@link Player}
     */
    public Player createPlayer(String name, String password) {
        return interactor.registerPlayer(name, password);
    }
}
