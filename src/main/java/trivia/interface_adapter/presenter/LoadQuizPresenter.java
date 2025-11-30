package trivia.interface_adapter.presenter;

import trivia.use_case.load_quiz.*;
import trivia.entity.Quiz;
import java.util.List;

public class LoadQuizPresenter implements LoadQuizOutputBoundary {
    private List<Quiz> quizzes;

    @Override
    public void present(LoadQuizResponseModel responseModel) {
        this.quizzes = responseModel.getQuizzes();
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }
}
