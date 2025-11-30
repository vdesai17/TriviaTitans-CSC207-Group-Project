package trivia.use_case.load_quiz;

import trivia.entity.Quiz;
import java.util.List;
import java.util.stream.Collectors;

public class LoadQuizInteractor implements LoadQuizInputBoundary {
    private final LoadQuizDataAccessInterface dataAccess;
    private final LoadQuizOutputBoundary presenter;

    public LoadQuizInteractor(LoadQuizDataAccessInterface dataAccess,
                             LoadQuizOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(LoadQuizInputData inputData) {
        try {
            List<Quiz> quizzes = dataAccess.getQuizzesByPlayer(inputData.getPlayerName());
            
            List<LoadQuizOutputData.QuizSummary> summaries = quizzes.stream()
                .map(quiz -> new LoadQuizOutputData.QuizSummary(
                    quiz.getId(),
                    quiz.getTitle(),
                    quiz.getCategory(),
                    quiz.getCreatorName().toString()
                ))
                .collect(Collectors.toList());
            
            LoadQuizOutputData outputData = new LoadQuizOutputData(summaries);
            presenter.presentQuizzes(outputData);
            
        } catch (Exception e) {
            presenter.presentError("Failed to load quizzes: " + e.getMessage());
        }
    }
}