package trivia.use_case.generate_from_wrong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.*;

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

        List<WrongQuestionRecord> distinctWrong = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (WrongQuestionRecord r : allWrong) {
            String key = r.getQuestionText() + "||" +
                    r.getCorrectAnswer() + "||" +
                    String.join("||", r.getOptions());
            if (seen.add(key)) {
                distinctWrong.add(r);
            }
        }

        int available = distinctWrong.size();
        if (requested > available) {
            presenter.prepareFailView("You currently have only " + available +
                    " wrong questions available. Please request at most " + available + " questions.");
            return;
        }

        List<WrongQuestionRecord> shuffled = new ArrayList<>(distinctWrong);
        Collections.shuffle(shuffled);
        List<WrongQuestionRecord> selected =
                shuffled.subList(0, requested);

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
