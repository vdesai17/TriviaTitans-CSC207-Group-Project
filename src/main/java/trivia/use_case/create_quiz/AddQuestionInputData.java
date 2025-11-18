package trivia.use_case.create_quiz;

import java.util.List;

public class AddQuestionInputData {

    private final String quizId;
    private final String questionText;
    private final List<String> options;
    private final String correctAnswer;
    private final String category;
    private final String difficulty;

    public AddQuestionInputData(String quizId,
                                String questionText,
                                List<String> options,
                                String correctAnswer,
                                String category,
                                String difficulty) {
        this.quizId = quizId;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.difficulty = difficulty;
    }

    public String getQuizId()        { return quizId; }
    public String getQuestionText()  { return questionText; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getCategory()      { return category; }
    public String getDifficulty()    { return difficulty; }
}
