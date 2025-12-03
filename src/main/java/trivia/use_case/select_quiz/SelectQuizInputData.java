package trivia.use_case.select_quiz;

/**
 * Immutable input data object for the "Select Quiz" use case.
 *
 * <p>This class groups together all the information coming from the
 * Controller that the interactor needs:
 * <ul>
 *     <li>categoryId – which category the user selected</li>
 *     <li>difficulty – difficulty level selected by the user</li>
 *     <li>amount – how many questions to load</li>
 * </ul>
 *
 * <p>Using a simple data object like this keeps the interactor signature
 * clean and makes it easy to extend later (e.g., adding new fields).
 */

public class SelectQuizInputData {

    /** The selected category ID (as used by the external API). */
    private final String categoryId;

    /** The selected difficulty level (e.g., "easy", "medium", "hard"). */
    private final String difficulty;

    /** The number of questions to fetch for this quiz. */
    private final int amount;

    /**
     * Creates a new input data object for the "Select Quiz" use case.
     *
     * @param categoryId the category identifier chosen by the user
     * @param difficulty the difficulty level chosen by the user
     * @param amount     number of questions to load
     */

    public SelectQuizInputData(String categoryId, String difficulty, int amount) {
        this.categoryId = categoryId;
        this.difficulty = difficulty;
        this.amount = amount;
    }

    /**
     * @return the API-specific category identifier
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * @return the selected difficulty level
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * @return how many questions should be fetched
     */
    public int getAmount() {
        return amount;
    }
}