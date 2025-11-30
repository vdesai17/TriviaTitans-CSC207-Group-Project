package trivia.use_case.login;

import trivia.entity.Player;

public class LoginOutputData {
    private final Player player;

    public LoginOutputData(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}