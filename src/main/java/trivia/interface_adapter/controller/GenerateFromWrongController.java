package trivia.interface_adapter.controller;

import trivia.use_case.generate_from_wrong.GenerateFromWrongInputBoundary;
import trivia.use_case.generate_from_wrong.GenerateFromWrongInputData;

/**
 * Controller for Use Case 6:
 * Generate a quiz from the user's past wrong questions.
 */
public class GenerateFromWrongController {

    private final GenerateFromWrongInputBoundary interactor;

    public GenerateFromWrongController(GenerateFromWrongInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Called by the UI (e.g., a button) when the user wants to
     * generate a quiz from their past wrong questions.
     *
     * @param playerName                 current player's name
     * @param requestedNumberOfQuestions how many questions the user wants;
     *                                   if <= 0, the interactor will treat it as "use all".
     */
    public void generate(String playerName, int requestedNumberOfQuestions) {
        GenerateFromWrongInputData inputData =
                new GenerateFromWrongInputData(playerName, requestedNumberOfQuestions);
        interactor.execute(inputData);
    }
}
