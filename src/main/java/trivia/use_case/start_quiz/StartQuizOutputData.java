package trivia.use_case.start_quiz;

import trivia.entity.Question;
import java.util.List;

public class StartQuizOutputData {
    private final List<Question> questions;
    private final String quizTitle;

    public StartQuizOutputData(List<Question> questions, String quizTitle) {
        this.questions = questions;
        this.quizTitle = quizTitle;
    }

    public List<Question> getQuestions() { return questions; }
    public String getQuizTitle() { return quizTitle; }
}