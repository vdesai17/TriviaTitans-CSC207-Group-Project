package trivia.use_case.load_quiz;

import trivia.entity.Quiz;
import java.util.List;

public interface LoadQuizDataAccessInterface {
    List<Quiz> getQuizzesByPlayer(String playerName);
}
