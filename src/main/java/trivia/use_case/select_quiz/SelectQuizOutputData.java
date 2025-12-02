package trivia.use_case.select_quiz;

import trivia.entity.Question;
import java.util.List;

public class SelectQuizOutputData {
    private final List<Question> questions;

    public SelectQuizOutputData(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}