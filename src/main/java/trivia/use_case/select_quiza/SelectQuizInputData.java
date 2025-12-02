package trivia.use_case.select_quiza;

public class SelectQuizInputData {
    private final String categoryId;
    private final String difficulty;
    private final int amount;

    public SelectQuizInputData(String categoryId, String difficulty, int amount) {
        this.categoryId = categoryId;
        this.difficulty = difficulty;
        this.amount = amount;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getAmount() {
        return amount;
    }
}