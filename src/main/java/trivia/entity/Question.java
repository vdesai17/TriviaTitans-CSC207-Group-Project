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
    
    /**
     * Returns the index of the correct answer in the options list.
     * Returns -1 if the correct answer is not found in the options.
     */
    public int getCorrectOptionIndex() {
        if (options == null || correctAnswer == null) {
            return -1;
        }
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).equals(correctAnswer)) {
                return i;
            }
        }
        return -1;
    }
}
