package trivia.use_case.create_quiz;

import trivia.entity.Question;
import trivia.entity.Quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Interactor for the Create Quiz use case.
 *
 *  convert question input to question entity
 *  generate quizId, create Quiz
 *  call data access interface to save quiz
 *  call Presenter to print result
 */
public class CreateQuizInteractor implements CreateQuizInputBoundary {

    private final CreateQuizDataAccessInterface quizDataAccess;
    private final CreateQuizOutputBoundary presenter;

    public CreateQuizInteractor(CreateQuizDataAccessInterface quizDataAccess,
                                CreateQuizOutputBoundary presenter) {
        this.quizDataAccess = quizDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(CreateQuizInputData inputData) {

        //all information needed for creating quiz
        if (inputData.getTitle() == null || inputData.getTitle().trim().isEmpty()) {
            presenter.prepareFailView("Quiz title cannot be empty.");
            return;
        }

        if (inputData.getCreatorName() == null || inputData.getCreatorName().trim().isEmpty()) {
            presenter.prepareFailView("Creator name cannot be empty.");
            return;
        }

        List<AddQuestionInputData> questionInputs = inputData.getQuestions();
        if (questionInputs == null || questionInputs.isEmpty()) {
            presenter.prepareFailView("Quiz must contain at least one question.");
            return;
        }

        // AddQuestionInputData to Question entity
        List<Question> questions = new ArrayList<>();

        // check all questions in the quiz
        for (AddQuestionInputData qInput : questionInputs) {

                if (qInput.getQuestionText() == null || qInput.getQuestionText().trim().isEmpty()) {
                    presenter.prepareFailView("Question text cannot be empty.");
                    return;
                }

                List<String> options = qInput.getOptions();
                if (options == null || options.size() < 2) {
                    presenter.prepareFailView("Each question must have at least two options.");
                    return;
                }

                String correctAnswer = qInput.getCorrectAnswer();
                if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
                    presenter.prepareFailView("Correct answer cannot be empty.");
                    return;
                }
                if (!options.contains(correctAnswer)) {
                    presenter.prepareFailView("Correct answer must be one of the options.");
                    return;
                }

                // if question has no category/difficulty, generate by quiz's category
                String questionCategory =
                        (qInput.getCategory() == null || qInput.getCategory().isEmpty())
                                ? inputData.getCategory()
                                : qInput.getCategory();

            String questionDifficulty =
                    (qInput.getDifficulty() == null || qInput.getDifficulty().isEmpty())
                            ? inputData.getDifficulty()
                            : qInput.getDifficulty();

            // generate question id for each question
            String questionId = UUID.randomUUID().toString();

            Question question = new Question(
                    questionId,
                    qInput.getQuestionText(),
                    options,
                    correctAnswer,
                    questionCategory,
                    questionDifficulty
            );

            questions.add(question);
        }

        // generate quiz id and quiz entity
        String quizId = UUID.randomUUID().toString();
        boolean existed = quizDataAccess.existsById(quizId);

        Quiz quiz = new Quiz(
                quizId,
                inputData.getTitle(),
                inputData.getCategory(),
                inputData.getDifficulty(),
                inputData.getCreatorName(),
                questions
        );

        // save quiz
        quizDataAccess.save(quiz);

        // store in outputdata for presenter
        CreateQuizOutputData outputData = new CreateQuizOutputData(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getCategory(),
                quiz.getDifficulty(),
                quiz.getQuestions().size(),
                !existed
        );

        presenter.prepareSuccessView(outputData);
    }
}
