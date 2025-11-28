package trivia.use_case.complete_quiz;

public class CompleteQuizOutputData {
    private final int score;
    private final int total;

    public CompleteQuizOutputData(int score, int total) {
        this.score = score;
        this.total = total;
    }

    public int getScore() { return score; }
    public int getTotal() { return total; }
}
