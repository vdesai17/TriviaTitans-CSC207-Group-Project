package trivia.interface_adapter.presenter;

import trivia.entity.Player;
import trivia.use_case.login.LoginOutputBoundary;
import trivia.use_case.login.LoginOutputData;

public class LoginPresenter implements LoginOutputBoundary {
    private final LoginViewModel viewModel;

    public LoginPresenter(LoginViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentLoginSuccess(LoginOutputData outputData) {
        viewModel.setLoginSuccess(outputData.getPlayer());
    }

    @Override
    public void presentLoginFailure(String errorMessage) {
        viewModel.setLoginFailure(errorMessage);
    }
}