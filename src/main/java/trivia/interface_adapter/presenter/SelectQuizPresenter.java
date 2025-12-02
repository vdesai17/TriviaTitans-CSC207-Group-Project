package trivia.interface_adapter.presenter;

import trivia.use_case.select_quiza.SelectQuizOutputBoundary;
import trivia.use_case.select_quiza.SelectQuizOutputData;

public class SelectQuizPresenter implements SelectQuizOutputBoundary {

    private final SelectQuizViewModel viewModel;

    public SelectQuizPresenter(SelectQuizViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentSuccess(SelectQuizOutputData outputData) {
        viewModel.setQuestions(outputData.getQuestions());
        viewModel.setErrorMessage(null);
        viewModel.firePropertyChanged();
    }

    @Override
    public void presentFailure(String errorMessage) {
        viewModel.setQuestions(null);
        viewModel.setErrorMessage(errorMessage);
        viewModel.firePropertyChanged();
    }
}