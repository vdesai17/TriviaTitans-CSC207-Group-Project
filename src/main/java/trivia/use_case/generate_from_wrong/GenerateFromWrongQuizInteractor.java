package trivia.use_case.generate_from_wrong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interactor for Use Case 6: generate a new quiz from the user's wrong questions.
 */
public class GenerateFromWrongQuizInteractor implements GenerateFromWrongInputBoundary {

    private final GenerateFromWrongDataAccessInterface dataAccess;
    private final GenerateFromWrongOutputBoundary presenter;

    public GenerateFromWrongQuizInteractor(GenerateFromWrongDataAccessInterface dataAccess,
                                           GenerateFromWrongOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(GenerateFromWrongInputData inputData) {
        String playerName = inputData.getPlayerName();
        int requested = inputData.getRequestedNumber();

        if (playerName == null || playerName.isBlank()) {
            presenter.prepareFailView("Player name must not be empty.");
            return;
        }
        if (requested <= 0) {
            presenter.prepareFailView("Number of questions must be positive.");
            return;
        }

        List<WrongQuestionRecord> allWrong =
                dataAccess.getWrongQuestionsForPlayer(playerName);

        if (allWrong.isEmpty()) {
            presenter.prepareFailView("No wrong questions found for player: " + playerName);
            return;
        }

        List<WrongQuestionRecord> shuffled = new ArrayList<>(allWrong);
        Collections.shuffle(shuffled);
        List<WrongQuestionRecord> selected =
                shuffled.subList(0, Math.min(requested, shuffled.size()));

        String quizId = dataAccess.createQuizFromWrongQuestions(playerName, selected);
        if (quizId == null) {
            presenter.prepareFailView("Failed to create practice quiz.");
            return;
        }

        List<String> texts = new ArrayList<>();
        for (WrongQuestionRecord record : selected) {
            texts.add(record.getQuestionText());
        }
        GenerateFromWrongOutputData outputData =
                new GenerateFromWrongOutputData(quizId, selected.size(), texts);

        presenter.prepareSuccessView(outputData);
    }
}
