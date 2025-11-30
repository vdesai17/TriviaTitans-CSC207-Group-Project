package trivia.interface_adapter.controller;

import trivia.use_case.login.LoginInputBoundary;
import trivia.use_case.login.LoginInputData;

public class LoginController {
    private final LoginInputBoundary interactor;

    public LoginController(LoginInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void login(String username, String password) {
        LoginInputData inputData = new LoginInputData(username, password);
        interactor.execute(inputData);
    }
}