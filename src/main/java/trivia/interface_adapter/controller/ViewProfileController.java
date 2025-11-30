package trivia.interface_adapter.controller;

import trivia.use_case.view_profile.ViewProfileInputBoundary;
import trivia.use_case.view_profile.ViewProfileInputData;

public class ViewProfileController {

    private final ViewProfileInputBoundary interactor;

    public ViewProfileController(ViewProfileInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String playerName) {
        ViewProfileInputData inputData = new ViewProfileInputData(playerName);
        interactor.execute(inputData);
    }
}