package trivia.use_case.select_quiza;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import trivia.interface_adapter.api.APIManager;
import trivia.interface_adapter.presenter.SelectQuizPresenter;
import trivia.interface_adapter.presenter.SelectQuizViewModel;
import trivia.entity.Question;

import java.util.List;


public class SelectQuizInteractorTest {

    @Test
    void testExecuteLoadQuestionsReturnsList() {
        // Arrange
        APIManager apiManager = new APIManager();
        SelectQuizViewModel viewModel = new SelectQuizViewModel();
        SelectQuizPresenter presenter = new SelectQuizPresenter(viewModel);
        SelectQuizInteractor interactor = new SelectQuizInteractor(apiManager, presenter);
        
        SelectQuizInputData inputData = new SelectQuizInputData("21", "medium", 3);

        // Act
        interactor.execute(inputData);
        
        // Assert
        List<Question> questions = viewModel.getQuestions();
        assertNotNull(questions);
        assertFalse(questions.isEmpty(), "Question list should not be empty");
        assertNotNull(questions.get(0).getQuestionText(), "Each question should have text");
    }
}
