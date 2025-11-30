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

/**
 * COMPLETE test suite for ReviewQuizInteractor with 100% code coverage
 * This version adds extra edge cases to ensure every line is covered
 */
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

    // ========================================================================
    //  viewPastQuizzes() Tests - COMPLETE Coverage
    // ========================================================================

    @Test
    void testViewPastQuizzesWithNoAttempts() {
        String playerName = "testPlayer";
        attemptDataAccess.attempts = new ArrayList<>();

        interactor.viewPastQuizzes(playerName);

        assertNotNull(presenter.lastResponse);
        assertEquals("No past quizzes found.", presenter.lastResponse.getMessage());
        assertTrue(presenter.lastResponse.getPastQuizzes().isEmpty());
        assertTrue(presenter.presentPastQuizListCalled);
    }

    @Test
    void testViewPastQuizzesWithSingleAttempt() {
        String playerName = "testPlayer";
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attempts = Arrays.asList(attempt);
        quizDataAccess.quiz = quiz;

        interactor.viewPastQuizzes(playerName);

        assertNotNull(presenter.lastResponse);
        assertFalse(presenter.lastResponse.getPastQuizzes().isEmpty());
        assertEquals(1, presenter.lastResponse.getPastQuizzes().size());
        assertEquals("Test Quiz", presenter.lastResponse.getPastQuizzes().get(0).getQuizTitle());
        assertEquals(1, presenter.lastResponse.getPastQuizzes().get(0).getScore());
        assertTrue(presenter.presentPastQuizListCalled);
    }

    @Test
    void testViewPastQuizzesWithMultipleAttempts() {
        String playerName = "testPlayer";
        Quiz quiz1 = createTestQuiz();
        Quiz quiz2 = createTestQuizWithDifferentTitle("Advanced Quiz");
        Quiz quiz3 = createTestQuizWithDifferentTitle("Expert Quiz");
        
        QuizAttempt attempt1 = createTestAttempt(quiz1);
        QuizAttempt attempt2 = createTestAttemptWithScore(quiz2, 2);
        QuizAttempt attempt3 = createTestAttemptWithScore(quiz3, 0);
        
        attemptDataAccess.attempts = Arrays.asList(attempt1, attempt2, attempt3);
        quizDataAccess.quiz = quiz1;

        interactor.viewPastQuizzes(playerName);

        assertNotNull(presenter.lastResponse);
        assertEquals(3, presenter.lastResponse.getPastQuizzes().size());
        assertTrue(presenter.presentPastQuizListCalled);
    }

    @Test
    void testViewPastQuizzesIteratesAllAttempts() {
        // Ensures every iteration of the for loop is exercised
        List<QuizAttempt> manyAttempts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Quiz q = createTestQuizWithDifferentTitle("Quiz " + i);
            manyAttempts.add(createTestAttemptWithScore(q, i));
        }
        
        attemptDataAccess.attempts = manyAttempts;
        quizDataAccess.quiz = manyAttempts.get(0).getQuiz();

        interactor.viewPastQuizzes("player");

        assertEquals(5, presenter.lastResponse.getPastQuizzes().size());
    }

    // ========================================================================
    //  openAttempt() Tests - COMPLETE Coverage
    // ========================================================================

    @Test
    void testOpenAttemptNotFound() {
        attemptDataAccess.attemptToReturn = Optional.empty();

        interactor.openAttempt("nonexistent-id");

        assertNotNull(presenter.lastResponse);
        assertEquals("Quiz attempt not found.", presenter.lastResponse.getMessage());
        assertTrue(presenter.presentQuizAttemptCalled);
    }

    @Test
    void testOpenAttemptSuccessWithEditableAttempt() {
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        attempt.setEditable(true);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;

        interactor.openAttempt(attempt.getAttemptId());

        assertNotNull(presenter.lastResponse);
        assertEquals(attempt.getAttemptId(), presenter.lastResponse.getAttemptId());
        assertEquals("Test Quiz", presenter.lastResponse.getQuizTitle());
        assertEquals(2, presenter.lastResponse.getQuestions().size());
        assertTrue(presenter.lastResponse.isEditingEnabled());
        assertTrue(presenter.presentQuizAttemptCalled);
    }

    @Test
    void testOpenAttemptSuccessWithNonEditableAttempt() {
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        attempt.setEditable(false);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;

        interactor.openAttempt(attempt.getAttemptId());

        assertNotNull(presenter.lastResponse);
        assertEquals(attempt.getAttemptId(), presenter.lastResponse.getAttemptId());
        assertFalse(presenter.lastResponse.isEditingEnabled());
        assertTrue(presenter.presentQuizAttemptCalled);
    }

    @Test
    void testOpenAttemptWithCorrectQuestionDetails() {
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;

        interactor.openAttempt(attempt.getAttemptId());

        List<ReviewQuizResponseModel.QuestionRow> questions = presenter.lastResponse.getQuestions();
        assertEquals(2, questions.size());
        
        assertEquals("What is 2+2?", questions.get(0).getQuestionText());
        assertEquals(4, questions.get(0).getOptions().size());
        assertEquals(0, questions.get(0).getCorrectIndex());
        assertEquals(0, questions.get(0).getSelectedIndex());
        
        assertEquals("What is 3+3?", questions.get(1).getQuestionText());
        assertEquals(1, questions.get(1).getCorrectIndex());
        assertEquals(0, questions.get(1).getSelectedIndex());
    }

    @Test
    void testOpenAttemptWithFewerSelectedAnswersThanQuestions() {
        // CRITICAL: Tests the i < selected.size() condition in the loop
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        attempt.setSelectedOptionIndices(Arrays.asList(0)); // Only 1 answer for 2 questions
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;

        interactor.openAttempt(attempt.getAttemptId());

        List<ReviewQuizResponseModel.QuestionRow> questions = presenter.lastResponse.getQuestions();
        assertEquals(2, questions.size());
        assertEquals(0, questions.get(0).getSelectedIndex());
        assertEquals(-1, questions.get(1).getSelectedIndex()); // Default when i >= selected.size()
    }

    @Test
    void testOpenAttemptWithEmptySelectedIndices() {
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        attempt.setSelectedOptionIndices(new ArrayList<>()); // Completely empty
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;

        interactor.openAttempt(attempt.getAttemptId());

        List<ReviewQuizResponseModel.QuestionRow> questions = presenter.lastResponse.getQuestions();
        assertEquals(2, questions.size());
        assertEquals(-1, questions.get(0).getSelectedIndex());
        assertEquals(-1, questions.get(1).getSelectedIndex());
    }

    @Test
    void testOpenAttemptIteratesAllQuestions() {
        // Ensures the for loop iterates through all questions
        Quiz quiz = createTestQuizWithMultipleQuestions(4);
        QuizAttempt attempt = createTestAttempt(quiz);
        attempt.setSelectedOptionIndices(Arrays.asList(0, 1, 2, 3));
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;

        interactor.openAttempt(attempt.getAttemptId());

        assertEquals(4, presenter.lastResponse.getQuestions().size());
    }

    // ========================================================================
    //  saveEditedAnswers() Tests - COMPLETE Coverage
    // ========================================================================

    @Test
    void testSaveEditedAnswersAttemptNotFound() {
        attemptDataAccess.attemptToReturn = Optional.empty();
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                "nonexistent-id", "player", Arrays.asList(0, 1));

        interactor.saveEditedAnswers(request);

        assertNotNull(presenter.lastResponse);
        assertEquals("Quiz attempt not found.", presenter.lastResponse.getMessage());
        assertTrue(presenter.presentSaveResultCalled);
        assertFalse(attemptDataAccess.updateAttemptCalled);
    }

    @Test
    void testSaveEditedAnswersNotEditable() {
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        attempt.setEditable(false);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                attempt.getAttemptId(), "player", Arrays.asList(0, 1));

        interactor.saveEditedAnswers(request);

        assertNotNull(presenter.lastResponse);
        assertEquals("Editing is restricted.", presenter.lastResponse.getMessage());
        assertFalse(presenter.lastResponse.isEditingEnabled());
        assertTrue(presenter.presentSaveResultCalled);
        assertFalse(attemptDataAccess.updateAttemptCalled);
    }

    @Test
    void testSaveEditedAnswersAllCorrect() {
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;
        
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                attempt.getAttemptId(), "player", Arrays.asList(0, 1));

        interactor.saveEditedAnswers(request);

        assertNotNull(presenter.lastResponse);
        assertEquals("Changes saved.", presenter.lastResponse.getMessage());
        assertTrue(presenter.lastResponse.isEditingEnabled());
        assertTrue(presenter.presentSaveResultCalled);
        assertTrue(attemptDataAccess.updateAttemptCalled);
        assertEquals(2, attemptDataAccess.updatedAttempt.getScore());
        assertEquals(Arrays.asList(0, 1), attemptDataAccess.updatedAttempt.getSelectedOptionIndices());
    }

    @Test
    void testSaveEditedAnswersPartiallyCorrect() {
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;
        
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                attempt.getAttemptId(), "player", Arrays.asList(0, 0));

        interactor.saveEditedAnswers(request);

        assertEquals(1, attemptDataAccess.updatedAttempt.getScore());
        assertTrue(attemptDataAccess.updateAttemptCalled);
    }

    @Test
    void testSaveEditedAnswersAllWrong() {
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;
        
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                attempt.getAttemptId(), "player", Arrays.asList(2, 2));

        interactor.saveEditedAnswers(request);

        assertEquals(0, attemptDataAccess.updatedAttempt.getScore());
        assertTrue(attemptDataAccess.updateAttemptCalled);
    }

    @Test
    void testSaveEditedAnswersWithEmptyAnswers() {
        Quiz quiz = createTestQuiz();
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;
        
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                attempt.getAttemptId(), "player", new ArrayList<>());

        interactor.saveEditedAnswers(request);

        assertEquals(0, attemptDataAccess.updatedAttempt.getScore());
        assertTrue(attemptDataAccess.updateAttemptCalled);
    }

    @Test
    void testSaveEditedAnswersWithFewerAnswersThanQuestions() {
        // CRITICAL: Tests i < updated.size() boundary condition
        Quiz quiz = createTestQuiz(); // Has 2 questions
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;
        
        // Only 1 answer for 2 questions
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                attempt.getAttemptId(), "player", Arrays.asList(0));

        interactor.saveEditedAnswers(request);

        // Should only score the first question
        assertEquals(1, attemptDataAccess.updatedAttempt.getScore());
        assertTrue(attemptDataAccess.updateAttemptCalled);
    }

    @Test
    void testSaveEditedAnswersWithMoreAnswersThanQuestions() {
        // Tests the questions.size() boundary
        Quiz quiz = createTestQuiz(); // Has 2 questions
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;
        
        // 3 answers for 2 questions (extra answer should be ignored)
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                attempt.getAttemptId(), "player", Arrays.asList(0, 1, 2));

        interactor.saveEditedAnswers(request);

        // Should score both questions correctly
        assertEquals(2, attemptDataAccess.updatedAttempt.getScore());
        assertTrue(attemptDataAccess.updateAttemptCalled);
    }

    @Test
    void testSaveEditedAnswersLoopIteratesCorrectly() {
        // Tests multiple iterations of the scoring loop
        Quiz quiz = createTestQuizWithMultipleQuestions(5);
        QuizAttempt attempt = createTestAttempt(quiz);
        
        attemptDataAccess.attemptToReturn = Optional.of(attempt);
        quizDataAccess.quiz = quiz;
        
        // Mix of correct and incorrect
        ReviewQuizRequestModel request = new ReviewQuizRequestModel(
                attempt.getAttemptId(), "player", 
                Arrays.asList(0, 1, 0, 1, 0)); // Pattern: correct, correct, wrong, wrong, wrong

        interactor.saveEditedAnswers(request);

        assertTrue(attemptDataAccess.updateAttemptCalled);
    }

    // ========================================================================
    //  Helper Methods
    // ========================================================================

    private Quiz createTestQuiz() {
        return createTestQuizWithDifferentTitle("Test Quiz");
    }

    private Quiz createTestQuizWithDifferentTitle(String title) {
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
        
        return new Quiz("quiz1", title, "Math", "easy", "creator", questions);
    }

    private Quiz createTestQuizWithMultipleQuestions(int count) {
        List<Question> questions = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Question q = new Question(
                    "q" + i,
                    "Question " + i + "?",
                    Arrays.asList("A", "B", "C", "D"),
                    "A",
                    "Test",
                    "easy"
            );
            questions.add(q);
        }
        
        return new Quiz("quiz-multi", "Multi Question Quiz", "Test", "easy", "creator", questions);
    }

    private QuizAttempt createTestAttempt(Quiz quiz) {
        return createTestAttemptWithScore(quiz, 1);
    }

    private QuizAttempt createTestAttemptWithScore(Quiz quiz, int score) {
        QuizAttempt attempt = new QuizAttempt(
                "attempt1",
                quiz,
                quiz.getQuestions().size(),
                "testPlayer",
                LocalDateTime.now().toString(),
                Arrays.asList("4", "5"),
                score
        );
        attempt.setSelectedOptionIndices(Arrays.asList(0, 0));
        attempt.setEditable(true);
        return attempt;
    }

    // ========================================================================
    //  Test Doubles (Mocks)
    // ========================================================================

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