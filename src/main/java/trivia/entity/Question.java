package trivia.entity;
import java.util.List;

public class Question {
    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private String category;
    private String difficulty;

    public Question(String questionText, List<String> options, String correctAnswer,
                    String category, String difficulty) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.difficulty = difficulty;
    }

    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getCategory() { return category; }
    public String getDifficulty() { return difficulty; }
}
