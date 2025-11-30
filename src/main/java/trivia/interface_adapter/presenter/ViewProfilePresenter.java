package trivia.interface_adapter.presenter;

import trivia.use_case.view_profile.ViewProfileOutputBoundary;
import trivia.use_case.view_profile.ViewProfileOutputData;

public class ViewProfilePresenter implements ViewProfileOutputBoundary {

    private final ViewProfileViewModel viewModel;

    public ViewProfilePresenter(ViewProfileViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(ViewProfileOutputData outputData) {
        viewModel.setPlayerName(outputData.getPlayerName());
        viewModel.setTotalScore(outputData.getTotalScore());
        viewModel.setTotalAttempts(outputData.getTotalAttempts());
        viewModel.setRank(outputData.getRank());
        viewModel.setTotalPlayers(outputData.getTotalPlayers());
        viewModel.firePropertyChanged();
    }
}