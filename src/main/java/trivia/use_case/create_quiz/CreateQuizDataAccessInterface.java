package trivia.use_case.create_quiz;

import trivia.entity.Quiz;

public interface CreateQuizDataAccessInterface {

    boolean existsById(String quizId);

    void save(Quiz quiz);

}
