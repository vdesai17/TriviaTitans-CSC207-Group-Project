package trivia.interface_adapter.controller;

import trivia.entity.Player;
import trivia.use_case.register_player.RegisterPlayerInteractor;

public class PlayerController {
    private final RegisterPlayerInteractor interactor;

    public PlayerController(RegisterPlayerInteractor interactor) {
        this.interactor = interactor;
    }

    public Player createPlayer(String name) {
        return interactor.registerPlayer(name);
    }
}
