package trivia.use_case.create_quiz;

import trivia.entity.Quiz;
import java.util.List;

public interface CreateQuizDataAccessInterface {

    /** Save or update a quiz */
    void saveQuiz(Quiz quiz);

    /** Return all quizzes */
    List<Quiz> getAllQuizzes();

    /** Return quizzes created by a specific player */
    List<Quiz> getQuizzesByPlayer(String playerName);

}
