package trivia.use_case.review_quiz;

import trivia.entity.Quiz;

public interface ReviewQuizQuizDataAccessInterface {
    Quiz getQuizById(String quizId);
}
