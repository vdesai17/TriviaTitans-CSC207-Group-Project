package trivia.use_case.start_quiz;

import trivia.entity.Quiz;
import trivia.interface_adapter.dao.QuizDataAccessObject;

public class StartQuizInteractor {
    private final QuizDataAccessObject quizDAO;

    public StartQuizInteractor(QuizDataAccessObject quizDAO) {
        this.quizDAO = quizDAO;
    }

    public StartQuizOutputData execute(StartQuizInputData inputData) {
        Quiz quiz = quizDAO.getQuizById(inputData.getQuizId());
        
        if (quiz == null) {
            throw new RuntimeException("Quiz not found: " + inputData.getQuizId());
        }
        
        return new StartQuizOutputData(quiz.getQuestions(), quiz.getTitle());
    }
}