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

        // Get data from the input object
        List<Question> questions = input.getQuestions();
        List<String> answers = input.getUserAnswers();
        String playerName = input.getPlayerName();  // current player's name

        // 1. Compute the score
        int score = 0;
        List<Integer> selectedOptionIndices = new java.util.ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String userAnswer = answers.get(i);
            int selectedIndex = -1;

            List<String> opts = question.getOptions();
            if (opts != null) {
                for (int idx = 0; idx < opts.size(); idx++) {
                    if (opts.get(idx).equals(userAnswer)) {
                        selectedIndex = idx;
                        break;
                    }
                }
            }
            selectedOptionIndices.add(selectedIndex);

            if (question.getCorrectAnswer().equals(userAnswer)) {
                score++;
            }
        }

        // 2. Build a Quiz object as a snapshot of this quiz
        String quizId = "quiz-" + System.currentTimeMillis();
        Quiz quiz = new Quiz(
                quizId,
                "API Quiz",   // title
                "general",    // category
                "mixed",      // difficulty
                playerName,   // record which player took this quiz
                questions
        );

        // 3. Build a QuizAttempt object and include the player name
        QuizAttempt attempt = new QuizAttempt(
                "attempt-" + System.currentTimeMillis(),
                quiz,
                questions.size(),
                playerName,          // store the player's name in userName
                LocalDateTime.now(),
                answers,
                score
        );
        attempt.setSelectedOptionIndices(selectedOptionIndices);

        // 4. Save this attempt to the repository
        repo.saveAttempt(attempt);

        // 5. Send the result to the presenter for the UI layer
        presenter.present(
                new CompleteQuizOutputData(score, questions.size())
        );
    }
}
