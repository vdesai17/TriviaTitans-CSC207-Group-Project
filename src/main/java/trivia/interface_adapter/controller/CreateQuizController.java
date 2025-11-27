package trivia.interface_adapter.controller;

import trivia.use_case.create_quiz.AddQuestionInputData;
import trivia.use_case.create_quiz.CreateQuizInputBoundary;
import trivia.use_case.create_quiz.CreateQuizInputData;

import java.util.List;

/**
 * Controller for the Create Quiz use case.
 * convert the UI input to the InputData and then call the Interactor。
 */
public class CreateQuizController {

    private final CreateQuizInputBoundary interactor;

    public CreateQuizController(CreateQuizInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * UI call this method to create the quiz
     */
    public void execute(String title,
                        String creatorName,
                        String category,
                        String difficulty,
                        List<AddQuestionInputData> questions) {

        // packaging UI input to the InputData
        CreateQuizInputData inputData = new CreateQuizInputData(
                title,
                creatorName,
                category,
                difficulty,
                questions
        );

        // call interactor to process InputData
        interactor.execute(inputData);
    }
}
