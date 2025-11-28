package trivia.interface_adapter.controller;

import trivia.use_case.generate_from_wrong.GenerateFromWrongInputBoundary;
import trivia.use_case.generate_from_wrong.GenerateFromWrongInputData;

/**
 * Controller for UC6.
 */
public class GenerateFromWrongController {

    private final GenerateFromWrongInputBoundary interactor;

    public GenerateFromWrongController(GenerateFromWrongInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void generate(String playerName, int requestedNumber) {
        GenerateFromWrongInputData data =
                new GenerateFromWrongInputData(playerName, requestedNumber);
        interactor.execute(data);
    }
}
