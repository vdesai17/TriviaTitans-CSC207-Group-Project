package trivia.use_case.select_quiz;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import trivia.interface_adapter.api.APIManager;
import trivia.entity.Question;

import java.util.List;


public class SelectQuizInteractorTest {

    @Test
    void testLoadQuestionsReturnsList() {
        // Arrange
        APIManager apiManager = new APIManager();
        SelectQuizInteractor interactor = new SelectQuizInteractor(apiManager);

        // Act
        List<Question> questions = interactor.loadQuestions("21", "medium", 3);

        // Assert
        assertNotNull(questions);
        assertFalse(questions.isEmpty(), "Question list should not be empty");
        assertNotNull(questions.get(0).getQuestionText(), "Each question should have text");
    }
}
