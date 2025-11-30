package trivia.interface_adapter.controller;

import trivia.use_case.create_quiz.AddQuestionInputData;
import trivia.use_case.create_quiz.CreateQuizInputBoundary;
import trivia.use_case.create_quiz.CreateQuizInputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Create Quiz use case.
 * Converts UI input to InputData and then calls the Interactor.
 */
public class CreateQuizController {

    private final CreateQuizInputBoundary interactor;

    public CreateQuizController(CreateQuizInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * UI calls this method to create the quiz.
     *
     * @param title         quiz title
     * @param creatorName   creator name
     * @param category      quiz category
     * @param difficulty    quiz difficulty
     * @param questionTexts list of question texts
     * @param optionsList   list of options list (same size as questionTexts)
     * @param correctIndexes list of correct option indexes for each question
     */
    public void execute(String title,
                        String creatorName,
                        String category,
                        String difficulty,
                        List<String> questionTexts,
                        List<List<String>> optionsList,
                        List<Integer> correctIndexes) {

        // 把“普通数据”转换成 use_case 的 AddQuestionInputData
        List<AddQuestionInputData> questionInputDataList = new ArrayList<>();

        for (int i = 0; i < questionTexts.size(); i++) {
            String qText = questionTexts.get(i);
            List<String> options = optionsList.get(i);
            int correctIdx = correctIndexes.get(i);

            String correctAnswer = options.get(correctIdx);

            AddQuestionInputData qInput = new AddQuestionInputData(
                    null,          // quizId 由 use case 决定
                    qText,
                    options,
                    correctAnswer,
                    category,
                    difficulty
            );
            questionInputDataList.add(qInput);
        }

        // ⚠ 注意：这里一定要按 CreateQuizInputData 的构造函数顺序来
        // 之前你的版本是 (title, creatorName, category, difficulty, questions)，顺序是错的
        CreateQuizInputData inputData = new CreateQuizInputData(
                title,
                category,
                difficulty,
                creatorName,
                questionInputDataList
        );

        interactor.execute(inputData);
    }
}
