package trivia.use_case.review_quiz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import trivia.entity.Question;
import trivia.entity.Quiz;
import trivia.entity.QuizAttempt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReviewQuizInteractorTest {

    private TestAttemptDataAccess attemptDataAccess;
    private TestQuizDataAccess quizDataAccess;
    private TestPresenter presenter;
    private ReviewQuizInteractor interactor;

    @BeforeEach
    void setUp() {
        attemptDataAccess = new TestAttemptDataAccess();
        quizDataAccess = new TestQuizDataAccess();
        presenter = new TestPresenter();
        interactor = new ReviewQuizInteractor(attemptDataAccess, quizDataAccess, presenter);
    }

    @Test
    void testViewPastQuizzesWithNoAttempts() {
        // Arrange
        String playerName = "testPlayer";
        attemptDataAccess.attempts = new ArrayList<>();

        // Act
        interactor.viewPastQuizzes(playerName);

        // Assert
        assertNotNull(presenter.lastResponse);
        assertEquals("No past quizzes found.", presenter.lastResponse.getMessage());
        assertTrue(presenter.lastResponse.getPastQuizzes().isEmpty());
        assertTrue(presenter.presentPastQuizListCalled);
    }

    @Test
    void testViewPastQuizzesWithAttempts() {
        // Arrange
        String playerName = "testPlayer";
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attempts = Arrays.asList(attempt);
        quizDataAccess.quiz = quiz;

        // Act
        interactor.viewPastQuizzes(playerName);

        // Assert
        assertNotNull(presenter.lastResponse);
        assertFalse(presenter.lastResponse.getPastQuizzes().isEmpty());
        assertEquals(1, presenter.lastResponse.getPastQuizzes().size());
        assertEquals("Test Quiz", presenter.lastResponse.getPastQuizzes().get(0).getQuizTitle());
        assertTrue(presenter.presentPastQuizListCalled);
    }

    @Test
    void testOpenAttemptNotFound() {
        // Arrange
        attemptDataAccess.attemptToReturn = Optional.empty();

        // Act
        interactor.openAttempt("nonexistent-id");

        // Assert
        assertNotNull(presenter.lastResponse);
        assertEquals("Quiz attempt not found.", presenter.lastResponse.getMessage());
        assertTrue(presenter.presentQuizAttemptCalled);
    }

    @Test
    void testOpenAttemptSuccess() {
        // Arrange
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;

        // Act
        interactor.openAttempt(attempt.getAttemptId());

        // Assert
        assertNotNull(presenter.lastResponse);
        assertEquals(attempt.getAttemptId(), presenter.lastResponse.getAttemptId());
        assertEquals("Test Quiz", presenter.lastResponse.getQuizTitle());
        assertEquals(2, presenter.lastResponse.getQuestions().size());
        assertTrue(presenter.lastResponse.isEditingEnabled());
        assertTrue(presenter.presentQuizAttemptCalled);
    }

    @Test
    void testSaveEditedAnswersAttemptNotFound() {
        // Arrange
        attemptDataAccess.attemptToReturn = Optional.empty();
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                "nonexistent-id", "player", Arrays.asList(0, 1));

        // Act
        interactor.saveEditedAnswers(request);

        // Assert
        assertNotNull(presenter.lastResponse);
        assertEquals("Quiz attempt not found.", presenter.lastResponse.getMessage());
        assertTrue(presenter.presentSaveResultCalled);
    }

    @Test
    void testSaveEditedAnswersNotEditable() {
        // Arrange
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        attempt.setEditable(false);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                attempt.getAttemptId(), "player", Arrays.asList(0, 1));

        // Act
        interactor.saveEditedAnswers(request);

        // Assert
        assertNotNull(presenter.lastResponse);
        assertEquals("Editing is restricted.", presenter.lastResponse.getMessage());
        assertFalse(presenter.lastResponse.isEditingEnabled());
        assertTrue(presenter.presentSaveResultCalled);
    }

    @Test
    void testSaveEditedAnswersSuccess() {
        // Arrange
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;
        
        // Change answers: first correct (0), second correct (1)
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                attempt.getAttemptId(), "player", Arrays.asList(0, 1));

        // Act
        interactor.saveEditedAnswers(request);

        // Assert
        assertNotNull(presenter.lastResponse);
        assertEquals("Changes saved.", presenter.lastResponse.getMessage());
        assertTrue(presenter.lastResponse.isEditingEnabled());
        assertTrue(presenter.presentSaveResultCalled);
        assertTrue(attemptDataAccess.updateAttemptCalled);
        assertEquals(2, attemptDataAccess.updatedAttempt.getScore()); // Both correct
    }

    @Test
    void testSaveEditedAnswersWithPartialCorrect() {
        // Arrange
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;
        
        // First correct (0), second wrong (0)
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                attempt.getAttemptId(), "player", Arrays.asList(0, 0));

        // Act
        interactor.saveEditedAnswers(request);

        // Assert
        assertEquals(1, attemptDataAccess.updatedAttempt.getScore()); // Only first correct
    }

    // Helper methods
    private Quiz createTestQuiz() {
        List<Question> questions = new ArrayList<>();
        
        Question q1 = new Question(
                "q1",
                "What is 2+2?",
                Arrays.asList("4", "5", "6", "7"),
                "4",
                "Math",
                "easy"
        );
        
        Question q2 = new Question(
                "q2",
                "What is 3+3?",
                Arrays.asList("5", "6", "7", "8"),
                "6",
                "Math",
                "easy"
        );
        
        questions.add(q1);
        questions.add(q2);
        
        return new Quiz("quiz1", "Test Quiz", "Math", "easy", "creator", questions);
    }

    private QuizAttempt createTestAttempt(Quiz quiz) {
        QuizAttempt attempt = new QuizAttempt(
                "attempt1",
                quiz,
                2,
                "testPlayer",
                LocalDateTime.now(),
                Arrays.asList("4", "5"),
                1
        );
        attempt.setSelectedOptionIndices(Arrays.asList(0, 0));
        attempt.setEditable(true);
        return attempt;
    }

    // Test doubles (mocks)
    private static class TestAttemptDataAccess implements ReviewQuizAttemptDataAccessInterface {
        List<QuizAttempt> attempts = new ArrayList<>();
        Optional<QuizAttempt> attemptToReturn = Optional.empty();
        boolean updateAttemptCalled = false;
        QuizAttempt updatedAttempt = null;

        @Override
        public List<QuizAttempt> getAttemptsForPlayer(String playerName) {
            return attempts;
        }

        @Override
        public Optional<QuizAttempt> getAttemptById(String attemptId) {
            return attemptToReturn;
        }

        @Override
        public void updateAttempt(QuizAttempt attempt) {
            updateAttemptCalled = true;
            updatedAttempt = attempt;
        }
    }

    private static class TestQuizDataAccess implements ReviewQuizQuizDataAccessInterface {
        Quiz quiz = null;

        @Override
        public Quiz getQuizById(String quizId) {
            return quiz;
        }
    }

    private static class TestPresenter implements ReviewQuizOutputBoundary {
        ReviewQuizResponseModel lastResponse = null;
        boolean presentPastQuizListCalled = false;
        boolean presentQuizAttemptCalled = false;
        boolean presentSaveResultCalled = false;

        @Override
        public void presentPastQuizList(ReviewQuizResponseModel responseModel) {
            presentPastQuizListCalled = true;
            lastResponse = responseModel;
        }

        @Override
        public void presentQuizAttempt(ReviewQuizResponseModel responseModel) {
            presentQuizAttemptCalled = true;
            lastResponse = responseModel;
        }

        @Override
        public void presentSaveResult(ReviewQuizResponseModel responseModel) {
            presentSaveResultCalled = true;
            lastResponse = responseModel;
        }
    }
}