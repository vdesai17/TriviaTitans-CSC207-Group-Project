package trivia.use_case.generate_from_wrong;

import java.util.List;

/**
 * A simple data holder representing one question that the player
 * previously answered incorrectly.
 *
 * This is on the use-case layer so that we don't depend on entity classes
 * like Question directly.
 */
public class WrongQuestionRecord {

    private final String questionId;     // optional: id of the question
    private final String quizId;         // optional: which quiz it came from
    private final String questionText;
    private final List<String> options;  // multiple choice options
    private final String correctAnswer;

    public WrongQuestionRecord(String questionId,
                               String quizId,
                               String questionText,
                               List<String> options,
                               String correctAnswer) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuizId() {
        return quizId;
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
