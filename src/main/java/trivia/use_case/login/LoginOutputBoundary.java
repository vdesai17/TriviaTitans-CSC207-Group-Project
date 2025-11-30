package trivia.use_case.login;

public interface LoginOutputBoundary {
    void presentLoginSuccess(LoginOutputData outputData);
    void presentLoginFailure(String errorMessage);
}