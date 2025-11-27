package trivia.entity;

import java.util.List;

/**
 * An immutable trivia question entity.
 */
public class Question {

    private final String id;             // unique identifier for the question

    private final String questionText;
    private final List<String> options;
    private final String correctAnswer;
    private final String category;
    private final String difficulty;

    public Question(String id,
                    String questionText,
                    List<String> options,
                    String correctAnswer,
                    String category,
                    String difficulty) {

        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.difficulty = difficulty;
    }

    public String getId() { return id; }

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
        if (options == null || correctAnswer == null) return -1;
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).equals(correctAnswer)) return i;
        }
        return -1;
    }
}
