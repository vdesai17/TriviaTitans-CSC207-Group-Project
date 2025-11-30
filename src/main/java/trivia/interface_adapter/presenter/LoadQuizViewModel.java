package trivia.interface_adapter.presenter;

import trivia.entity.Quiz;
import java.util.List;

public class LoadQuizViewModel {
    private List<Quiz> quizzes;

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }
}
