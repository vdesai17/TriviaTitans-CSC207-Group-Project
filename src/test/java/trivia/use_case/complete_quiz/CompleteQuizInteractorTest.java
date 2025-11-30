package trivia.use_case.complete_quiz;

import org.junit.jupiter.api.Test;
import trivia.entity.Question;
import trivia.entity.QuizAttempt;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CompleteQuizInteractor (UC5: Complete Quiz).
 */
class CompleteQuizInteractorTest {

    /**
     * Simple in-memory implementation of QuizAttemptDataAccessInterface
     * to capture saved attempts.
     */
    private static class InMemoryQuizAttemptRepo implements QuizAttemptDataAccessInterface {
        private final List<QuizAttempt> savedAttempts = new ArrayList<>();

        @Override
        public void saveAttempt(QuizAttempt attempt) {
            savedAttempts.add(attempt);
        }

        public List<QuizAttempt> getSavedAttempts() {
            return savedAttempts;
        }
    }

    /**
     * Test presenter that stores the last output data for assertions.
     */
    private static class TestPresenter implements CompleteQuizOutputBoundary {
        private CompleteQuizOutputData lastOutput;

        @Override
        public void present(CompleteQuizOutputData outputData) {
            this.lastOutput = outputData;
        }

        public CompleteQuizOutputData getLastOutput() {
            return lastOutput;
        }
    }

    /**
     * Helper to create a Question entity.
     */
    private Question createQuestion(String text, List<String> options, String correct) {
        return new Question(
                "q-" + text.hashCode(), // simple id
                text,
                options,
                correct,
                "Sports",
                "easy"
        );
    }

    @Test
    void execute_savesAttemptAndCallsPresenter_withCorrectScore() {
        // Arrange
        InMemoryQuizAttemptRepo repo = new InMemoryQuizAttemptRepo();
        TestPresenter presenter = new TestPresenter();
        CompleteQuizInteractor interactor = new CompleteQuizInteractor(repo, presenter);

        String playerName = "test-player";

        List<String> options1 = List.of("A", "B", "C", "D");
        List<String> options2 = List.of("True", "False", "Maybe", "Not sure");
        List<String> options3 = List.of("Red", "Green", "Blue", "Yellow");

        Question q1 = createQuestion("Q1", options1, "B");
        Question q2 = createQuestion("Q2", options2, "True");
        Question q3 = createQuestion("Q3", options3, "Red");

        List<Question> questions = List.of(q1, q2, q3);

        // User answers: 2 correct out of 3
        List<String> userAnswers = List.of(
                "B",      // correct
                "False",  // wrong
                "Red"     // correct
        );

        CompleteQuizInputData inputData =
                new CompleteQuizInputData(playerName, questions, userAnswers);

        // Act
        interactor.execute(inputData);

        // Assert: repo should have exactly one saved attempt
        List<QuizAttempt> attempts = repo.getSavedAttempts();
        assertEquals(1, attempts.size(), "One attempt should be saved.");

        QuizAttempt attempt = attempts.get(0);
        assertNotNull(attempt.getQuiz(), "Quiz inside attempt should not be null.");
        assertEquals(3, attempt.getTotalQuestions(), "Total questions should match.");
        assertEquals(playerName, attempt.getUserName(), "Player name should be stored in attempt.");
        assertEquals(2, attempt.getScore(), "Score should be 2 (2 correct out of 3).");
        assertEquals(userAnswers, attempt.getUserAnswers(), "User answers should be stored as given.");

        // Assert: presenter should receive correct output data
        CompleteQuizOutputData output = presenter.getLastOutput();
        assertNotNull(output, "Presenter should be called with output data.");
        assertEquals(2, output.getScore(), "Output score should match computed score.");
        assertEquals(3, output.getTotal(), "Output total should match number of questions.");
    }

    @Test
    void execute_allWrongAnswers_resultsInZeroScore() {
        // Arrange
        InMemoryQuizAttemptRepo repo = new InMemoryQuizAttemptRepo();
        TestPresenter presenter = new TestPresenter();
        CompleteQuizInteractor interactor = new CompleteQuizInteractor(repo, presenter);

        String playerName = "another-player";

        List<String> options = List.of("A", "B", "C", "D");

        Question q1 = createQuestion("Q1", options, "A");
        Question q2 = createQuestion("Q2", options, "B");

        List<Question> questions = List.of(q1, q2);

        // User answers: all wrong
        List<String> userAnswers = List.of(
                "C",  // wrong
                "D"   // wrong
        );

        CompleteQuizInputData inputData =
                new CompleteQuizInputData(playerName, questions, userAnswers);

        // Act
        interactor.execute(inputData);

        // Assert: attempt saved with score 0
        List<QuizAttempt> attempts = repo.getSavedAttempts();
        assertEquals(1, attempts.size(), "One attempt should be saved.");

        QuizAttempt attempt = attempts.get(0);
        assertEquals(0, attempt.getScore(), "Score should be 0 when all answers are wrong.");
        assertEquals(2, attempt.getTotalQuestions(), "Total questions should be 2.");
        assertEquals(playerName, attempt.getUserName(), "Player name should match.");

        // Assert: presenter output
        CompleteQuizOutputData output = presenter.getLastOutput();
        assertNotNull(output, "Presenter should be called.");
        assertEquals(0, output.getScore(), "Output score should be 0.");
        assertEquals(2, output.getTotal(), "Output total should be 2.");
    }
}
