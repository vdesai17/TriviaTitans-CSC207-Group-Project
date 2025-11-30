package trivia.use_case.login;

import trivia.entity.Player;

public interface LoginDataAccessInterface {
    Player validateLogin(String username, String password);
}