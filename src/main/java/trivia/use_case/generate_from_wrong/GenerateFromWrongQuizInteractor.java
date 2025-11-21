package trivia.use_case.generate_from_wrong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interactor (Use Case) for Use Case 6:
 * Generate a quiz from the user's past wrong questions.
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
        int requested = inputData.getRequestedNumberOfQuestions();

        if (playerName == null || playerName.trim().isEmpty()) {
            presenter.prepareFailView("Player name is missing.");
            return;
        }

        List<WrongQuestionRecord> allWrong =
                dataAccess.getWrongQuestionsForPlayer(playerName);

        if (allWrong == null || allWrong.isEmpty()) {
            presenter.prepareFailView("You have no past wrong questions yet.");
            return;
        }

        int totalAvailable = allWrong.size();

        // If requested <= 0, treat it as "use all".
        int numberToUse = requested <= 0 ? totalAvailable : requested;

        if (numberToUse > totalAvailable) {
            numberToUse = totalAvailable;
        }

        // Randomize the selection so that the quiz is not always the same.
        List<WrongQuestionRecord> shuffled = new ArrayList<>(allWrong);
        Collections.shuffle(shuffled);

        List<WrongQuestionRecord> selected = shuffled.subList(0, numberToUse);

        // Let the DAO actually create and store the quiz.
        String quizId = dataAccess.createQuizFromWrongQuestions(playerName, selected);

        // Prepare a simple view of the selected questions (only texts).
        List<String> questionTexts = new ArrayList<>();
        for (WrongQuestionRecord record : selected) {
            questionTexts.add(record.getQuestionText());
        }

        GenerateFromWrongOutputData outputData =
                new GenerateFromWrongOutputData(
                        quizId,
                        questionTexts,
                        requested,
                        totalAvailable
                );

        presenter.prepareSuccessView(outputData);
    }
}
