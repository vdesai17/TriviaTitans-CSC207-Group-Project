package trivia.use_case.view_profile;

import trivia.entity.Player;
import java.util.Comparator;
import java.util.List;

public class ViewProfileInteractor implements ViewProfileInputBoundary {

    private final ViewProfileDataAccessInterface dataAccess;
    private final ViewProfileOutputBoundary presenter;

    public ViewProfileInteractor(ViewProfileDataAccessInterface dataAccess,
                                 ViewProfileOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewProfileInputData inputData) {
        String playerName = inputData.getPlayerName();

        // Load current player
        Player player = dataAccess.loadPlayer(playerName);
        if (player == null) {
            presenter.present(new ViewProfileOutputData(playerName, 0, 0, -1, 0));
            return;
        }

        // Calculate stats
        int totalScore = player.getScore();
        int totalAttempts = (player.getPastAttempts() == null) ? 0 : player.getPastAttempts().size();

        // Calculate ranking
        List<Player> allPlayers = dataAccess.getAllPlayers();
        allPlayers.sort(Comparator.comparingInt(Player::getScore).reversed());

        int rank = -1;
        int position = 1;
        for (Player p : allPlayers) {
            if (p.getPlayerName().equalsIgnoreCase(playerName)) {
                rank = position;
                break;
            }
            position++;
        }

        ViewProfileOutputData outputData = new ViewProfileOutputData(
                playerName,
                totalScore,
                totalAttempts,
                rank,
                allPlayers.size()
        );

        presenter.present(outputData);
    }
}