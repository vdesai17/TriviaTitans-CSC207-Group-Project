package trivia.interface_adapter.presenter;

import trivia.use_case.create_quiz.CreateQuizOutputBoundary;
import trivia.use_case.create_quiz.CreateQuizOutputData;

/**
 * Presenter for the Create Quiz use case.
 * convert the output from Interactor to be ViewModel for UI uses it
 */
public class CreateQuizPresenter implements CreateQuizOutputBoundary {

    private final CreateQuizViewModel viewModel;

    public CreateQuizPresenter(CreateQuizViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(CreateQuizOutputData outputData) {
        viewModel.setQuizId(outputData.getQuizId());
        // 如果你只关心 quizId，也可以不设 title/category 等
        viewModel.setSuccess(true);
        viewModel.setErrorMessage(null);

        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.setSuccess(false);
        viewModel.setErrorMessage(errorMessage);

        viewModel.firePropertyChanged();
    }

}
