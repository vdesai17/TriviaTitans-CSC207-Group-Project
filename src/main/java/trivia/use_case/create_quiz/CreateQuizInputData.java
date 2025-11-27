package trivia.use_case.create_quiz;

import java.util.List;

/**
 * Input data for the Create Quiz use case.
 * Provided by the Controller when the user clicks "Save Quiz".
 */
public class CreateQuizInputData {

    private final String title;
    private final String category;
    private final String difficulty;
    private final String creatorName;
    private final List<AddQuestionInputData> questions;

    public CreateQuizInputData(String title,
                               String category,
                               String difficulty,
                               String creatorName,
                               List<AddQuestionInputData> questions) {
        this.title = title;
        this.category = category;
        this.difficulty = difficulty;
        this.creatorName = creatorName;
        this.questions = questions;
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

    public String getCreatorName() {
        return creatorName;
    }

    public List<AddQuestionInputData> getQuestions() {
        return questions;
    }
}
