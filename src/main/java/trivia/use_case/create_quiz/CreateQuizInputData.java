package trivia.use_case.create_quiz;

/**
 * Input Data that is needed to create a new Quiz
 */
public class CreateQuizInputData {

    private final String quizId;
    private final String title;
    private final String category;
    private final String difficulty;
    private final String creatorName;

    public CreateQuizInputData(String quizId,
                               String title,
                               String category,
                               String difficulty,
                               String creatorName) {
        this.quizId = quizId;
        this.title = title;
        this.category = category;
        this.difficulty = difficulty;
        this.creatorName = creatorName;
    }

    public String getQuizId()      { return quizId; }
    public String getTitle()       { return title; }
    public String getCategory()    { return category; }
    public String getDifficulty()  { return difficulty; }
    public String getCreatorName() { return creatorName; }
}
