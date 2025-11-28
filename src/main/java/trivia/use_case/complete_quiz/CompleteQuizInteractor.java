package trivia.use_case.complete_quiz;

import trivia.entity.Question;
import trivia.entity.Quiz;
import trivia.entity.QuizAttempt;

import java.time.LocalDateTime;
import java.util.List;

public class CompleteQuizInteractor implements CompleteQuizInputBoundary {

    private final QuizAttemptDataAccessInterface repo;
    private final CompleteQuizOutputBoundary presenter;

    public CompleteQuizInteractor(QuizAttemptDataAccessInterface repo,
                                  CompleteQuizOutputBoundary presenter) {
        this.repo = repo;
        this.presenter = presenter;
    }

    @Override
    public void execute(CompleteQuizInputData input) {

        List<Question> questions = input.getQuestions();
        List<String> answers = input.getUserAnswers();
        String playerName = input.getPlayerName();

        // Score
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getCorrectAnswer().equals(answers.get(i))) {
                score++;
            }
        }

        // Build quiz object
        String quizId = "quiz-" + System.currentTimeMillis();
        Quiz quiz = new Quiz(
                quizId,
                "API Quiz",
                "general",
                "mixed",
                playerName,
                questions
        );

        QuizAttempt attempt = new QuizAttempt(
                "attempt-" + System.currentTimeMillis(),
                quiz,
                questions.size(),
                playerName,
                LocalDateTime.now(),
                answers,
                score
        );

        repo.saveAttempt(attempt);

        presenter.present(
                new CompleteQuizOutputData(score, questions.size())
        );
    }
}
