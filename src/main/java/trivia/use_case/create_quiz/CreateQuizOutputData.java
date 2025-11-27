package trivia.use_case.create_quiz;

/**
 * Output data for the Create Quiz use case, passed from the interactor
 * to the presenter, containing the information needed by the UI.
 */
public class CreateQuizOutputData {

    private final String quizId;
    private final String title;
    private final String category;
    private final String difficulty;
    private final int questionCount;
    private boolean isSuccess;

    public CreateQuizOutputData(String quizId,
                                String title,
                                String category,
                                String difficulty,
                                int questionCount,
                                boolean isSuccess) {
        this.quizId = quizId;
        this.title = title;
        this.category = category;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.isSuccess = isSuccess;
    }

    public String getQuizId() {
        return quizId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        this.isSuccess = success;
    }
}
