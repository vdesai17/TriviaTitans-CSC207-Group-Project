package trivia.use_case.login;

import trivia.entity.Player;

public class LoginInteractor implements LoginInputBoundary {
    private final LoginDataAccessInterface dataAccess;
    private final LoginOutputBoundary presenter;

    public LoginInteractor(LoginDataAccessInterface dataAccess,
                          LoginOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(LoginInputData inputData) {
        try {
            Player player = dataAccess.validateLogin(
                inputData.getUsername(),
                inputData.getPassword()
            );

            if (player != null) {
                LoginOutputData outputData = new LoginOutputData(player);
                presenter.presentLoginSuccess(outputData);
            } else {
                presenter.presentLoginFailure("Invalid username or password");
            }
        } catch (Exception e) {
            presenter.presentLoginFailure("Login failed: " + e.getMessage());
        }
    }
}