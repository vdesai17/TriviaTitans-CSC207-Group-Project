package trivia.use_case.generate_from_wrong;

import java.util.List;

/**
 * A simple data structure representing one wrong question the player has done.
 */
public class WrongQuestionRecord {
    private final String sourceQuizId;
    private final String label;
    private final String questionText;
    private final List<String> options;
    private final String correctAnswer;

    public WrongQuestionRecord(String sourceQuizId,
                               String label,
                               String questionText,
                               List<String> options,
                               String correctAnswer) {
        this.sourceQuizId = sourceQuizId;
        this.label = label;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getSourceQuizId() {
        return sourceQuizId;
    }

    public String getLabel() {
        return label;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
