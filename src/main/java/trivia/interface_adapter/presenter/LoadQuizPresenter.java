package trivia.interface_adapter.presenter;

import trivia.use_case.load_quiz.LoadQuizOutputBoundary;
import trivia.use_case.load_quiz.LoadQuizOutputData;

public class LoadQuizPresenter implements LoadQuizOutputBoundary {
    private final LoadQuizViewModel viewModel;

    public LoadQuizPresenter(LoadQuizViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentQuizzes(LoadQuizOutputData outputData) {
        viewModel.setQuizzes(outputData.getQuizzes());
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setError(errorMessage);
    }
}