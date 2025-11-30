package trivia.use_case.load_quiz;

import trivia.entity.Quiz;
import java.util.List;

public class LoadQuizResponseModel {
    private final List<Quiz> quizzes;

    public LoadQuizResponseModel(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }
}
