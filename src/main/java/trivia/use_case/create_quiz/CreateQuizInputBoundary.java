package trivia.use_case.create_quiz;

public interface CreateQuizInputBoundary {

    void createQuiz(CreateQuizInputData inputData);

    void addQuestion(AddQuestionInputData inputData);
}
