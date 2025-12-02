package trivia.interface_adapter.controller;

import trivia.use_case.select_quiz.SelectQuizInputBoundary;
import trivia.use_case.select_quiz.SelectQuizInputData;
import trivia.use_case.select_quiz.SelectQuizOutputData;
import trivia.use_case.select_quiz.SelectQuizOutputBoundary;
import trivia.entity.Question;
import trivia.interface_adapter.presenter.SelectQuizViewModel;
import trivia.interface_adapter.presenter.SelectQuizPresenter;

import java.util.List;

/**
 * FIXED SelectQuizController - Now properly integrates with presenter and ViewModel
 * 
 * BEFORE: Called interactor directly, threw exceptions
 * AFTER: Creates presenter/ViewModel, properly handles responses
 * 
 * Clean Architecture: Controller → Interactor → Presenter → ViewModel → UI
 */
public class SelectQuizController {
    private final SelectQuizInputBoundary interactor;
    private final SelectQuizPresenter presenter;
    private final SelectQuizViewModel viewModel;

    /**
     * Full constructor - used by AppFactory
     */
    public SelectQuizController(SelectQuizInputBoundary interactor,
                               SelectQuizPresenter presenter,
                               SelectQuizViewModel viewModel) {
        this.interactor = interactor;
        this.presenter = presenter;
        this.viewModel = viewModel;
    }

    /**
     * Execute the select quiz use case
     * 
     * Input: categoryId, difficulty, amount
     * Flow: Controller → Interactor → API → Presenter → ViewModel → UI
     * 
     * The controller NEVER touches API or data directly - it delegates to interactor
     */
    public void execute(String categoryId, String difficulty, int amount) {
        SelectQuizInputData inputData = new SelectQuizInputData(categoryId, difficulty, amount);
        interactor.execute(inputData);
        // ViewModel is updated via presenter, UI listens via PropertyChangeListener
    }

    /**
     * Convenience method: Retrieve the questions from ViewModel after execute() completes
     * 
     * @return List of questions, or null if execution failed
     */
    public List<Question> getQuestionsFromViewModel() {
        return viewModel.getQuestions();
    }

    /**
     * Convenience method: Check if there was an error
     * 
     * @return Error message, or null if no error
     */
    public String getErrorFromViewModel() {
        return viewModel.getErrorMessage();
    }
}