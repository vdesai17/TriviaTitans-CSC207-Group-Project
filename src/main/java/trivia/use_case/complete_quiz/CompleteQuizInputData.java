package trivia.use_case.complete_quiz;

import trivia.entity.Question;

import java.util.List;

public class CompleteQuizInputData {

    private final String playerName;
    private final List<Question> questions;
    private final List<String> userAnswers;

    public CompleteQuizInputData(String playerName,
                                 List<Question> questions,
                                 List<String> userAnswers) {
        this.playerName = playerName;
        this.questions = questions;
        this.userAnswers = userAnswers;
    }

    public String getPlayerName() { return playerName; }
    public List<Question> getQuestions() { return questions; }
    public List<String> getUserAnswers() { return userAnswers; }
}
