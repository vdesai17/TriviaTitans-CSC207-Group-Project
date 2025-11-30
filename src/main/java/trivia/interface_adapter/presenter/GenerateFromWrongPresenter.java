package trivia.interface_adapter.presenter;

import trivia.use_case.generate_from_wrong.GenerateFromWrongOutputBoundary;
import trivia.use_case.generate_from_wrong.GenerateFromWrongOutputData;

import javax.swing.*;
import java.util.Collections;

/**
 * Presenter for Use Case 6.
 */
public class GenerateFromWrongPresenter implements GenerateFromWrongOutputBoundary {

    private final GenerateFromWrongViewModel viewModel;

    public GenerateFromWrongPresenter(GenerateFromWrongViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(GenerateFromWrongOutputData response) {
        GenerateFromWrongState state = viewModel.getState();
        state.setQuizId(response.getQuizId());
        state.setQuestionTexts(response.getQuestionTexts());
        state.setRequestedNumber(response.getNumberOfQuestions());
        state.setErrorMessage(null);

        viewModel.setState(state);
        viewModel.fireStateChanged();


        JOptionPane.showMessageDialog(null,
                "Generated practice quiz: " + response.getQuizId() +
                        "\n(" + response.getNumberOfQuestions() + " questions)",
                "Practice Quiz Ready",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void prepareFailView(String errorMessage) {
        GenerateFromWrongState state = viewModel.getState();
        state.setQuizId(null);
        state.setQuestionTexts(Collections.emptyList());
        state.setRequestedNumber(0);
        state.setErrorMessage(errorMessage);

        viewModel.setState(state);
        viewModel.fireStateChanged();

        JOptionPane.showMessageDialog(null,
                errorMessage,
                "Cannot Generate Practice Quiz",
                JOptionPane.ERROR_MESSAGE);
    }
}
