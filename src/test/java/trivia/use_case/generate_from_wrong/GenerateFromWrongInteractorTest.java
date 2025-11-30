package trivia.use_case.generate_from_wrong;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UC6 interactor: GenerateFromWrongQuizInteractor.
 */
class GenerateFromWrongQuizInteractorTest {

    /**
     * Happy path: user has enough wrong questions and a practice quiz is created.
     */
    @Test
    void generatesPracticeQuizWhenEnoughWrongQuestions() {
        // given
        List<WrongQuestionRecord> wrongQuestions = Arrays.asList(
                new WrongQuestionRecord(
                        "quiz-1",
                        "Quiz 1",
                        "Q1?",
                        Arrays.asList("A", "B", "C", "D"),
                        "A"
                ),
                new WrongQuestionRecord(
                        "quiz-1",
                        "Quiz 1",
                        "Q2?",
                        Arrays.asList("A", "B", "C", "D"),
                        "B"
                ),
                new WrongQuestionRecord(
                        "quiz-2",
                        "Quiz 2",
                        "Q3?",
                        Arrays.asList("A", "B", "C", "D"),
                        "C"
                )
        );

        TestDAO dao = new TestDAO(wrongQuestions, false);
        TestPresenter presenter = new TestPresenter();

        GenerateFromWrongQuizInteractor interactor =
                new GenerateFromWrongQuizInteractor(dao, presenter);

        GenerateFromWrongInputData input =
                new GenerateFromWrongInputData("test-user", 2);

        // when
        interactor.execute(input);

        // then
        assertTrue(presenter.successCalled, "Success view should be called.");
        assertFalse(presenter.failCalled, "Fail view should not be called.");

        // DAO should receive exactly requested number of questions (2)
        assertEquals(2, dao.lastCreatedQuestions.size(),
                "DAO should be called with the requested number of questions.");

        // Output data should be populated correctly
        assertNotNull(presenter.lastSuccessOutput, "Output data should not be null.");
        assertNotNull(presenter.lastSuccessOutput.getQuizId(), "Quiz id should not be null.");
        assertEquals(2, presenter.lastSuccessOutput.getNumberOfQuestions(),
                "Output should contain the selected number of questions.");
        assertEquals(2, presenter.lastSuccessOutput.getQuestionTexts().size(),
                "Question text list size should match numberOfQuestions.");
    }

    /**
     * If the user requests more questions than available, we now FAIL with a clear message
     * and do NOT create a quiz.
     */
    @Test
    void failsWhenRequestedNumberGreaterThanAvailable() {
        // given only 2 distinct wrong questions
        List<WrongQuestionRecord> wrongQuestions = Arrays.asList(
                new WrongQuestionRecord(
                        "quiz-1",
                        "Quiz 1",
                        "Q1?",
                        Arrays.asList("A", "B", "C", "D"),
                        "A"
                ),
                new WrongQuestionRecord(
                        "quiz-1",
                        "Quiz 1",
                        "Q2?",
                        Arrays.asList("A", "B", "C", "D"),
                        "B"
                )
        );

        TestDAO dao = new TestDAO(wrongQuestions, false);
        TestPresenter presenter = new TestPresenter();
        GenerateFromWrongQuizInteractor interactor =
                new GenerateFromWrongQuizInteractor(dao, presenter);

        // request 5, but only 2 available
        GenerateFromWrongInputData input =
                new GenerateFromWrongInputData("test-user", 5);

        // when
        interactor.execute(input);

        // then
        assertFalse(presenter.successCalled, "Success view should NOT be called.");
        assertTrue(presenter.failCalled, "Fail view should be called.");

        assertNotNull(presenter.lastErrorMessage, "Error message should not be null.");
        assertTrue(
                presenter.lastErrorMessage.contains("only 2 wrong questions"),
                "Error message should mention the available number of questions."
        );

        // We should NOT attempt to create a quiz in the DAO
        assertNull(dao.lastCreatedQuizId, "DAO should not be asked to create a quiz.");
        assertEquals(0, dao.lastCreatedQuestions.size(),
                "DAO should not receive any questions when failing early.");
    }

    /**
     * Duplicated wrong questions should be removed before sampling.
     * We check that DAO only receives distinct questions.
     */
    @Test
    void removesDuplicateWrongQuestionsBeforeSelecting() {
        List<String> options = Arrays.asList("A", "B", "C", "D");

        List<WrongQuestionRecord> wrongQuestions = Arrays.asList(
                new WrongQuestionRecord(
                        "quiz-1",
                        "Quiz 1",
                        "Q1?",
                        options,
                        "A"
                ),
                // exact duplicate of the first one
                new WrongQuestionRecord(
                        "quiz-1",
                        "Quiz 1",
                        "Q1?",
                        options,
                        "A"
                ),
                new WrongQuestionRecord(
                        "quiz-2",
                        "Quiz 2",
                        "Q2?",
                        options,
                        "B"
                )
        );

        TestDAO dao = new TestDAO(wrongQuestions, false);
        TestPresenter presenter = new TestPresenter();
        GenerateFromWrongQuizInteractor interactor =
                new GenerateFromWrongQuizInteractor(dao, presenter);

        // We request 2; there are 3 records but only 2 distinct questions
        GenerateFromWrongInputData input =
                new GenerateFromWrongInputData("test-user", 2);

        // when
        interactor.execute(input);

        // then
        assertTrue(presenter.successCalled, "Success view should be called.");
        assertFalse(presenter.failCalled, "Fail view should not be called.");

        // DAO should receive exactly 2 questions (no duplicates)
        assertEquals(2, dao.lastCreatedQuestions.size(),
                "DAO should receive exactly the requested number of distinct questions.");

        long countQ1 = dao.lastCreatedQuestions.stream()
                .filter(r -> r.getQuestionText().equals("Q1?"))
                .count();

        // 因为去重逻辑，Q1? 最多只会出现一次
        assertTrue(countQ1 <= 1,
                "Duplicate wrong questions should be removed before creating the quiz.");
    }

    /**
     * If there are no wrong questions, the interactor should fail and not create a quiz.
     */
    @Test
    void failsWhenNoWrongQuestions() {
        // given: empty wrong question list
        List<WrongQuestionRecord> wrongQuestions = new ArrayList<>();

        TestDAO dao = new TestDAO(wrongQuestions, false);
        TestPresenter presenter = new TestPresenter();
        GenerateFromWrongQuizInteractor interactor =
                new GenerateFromWrongQuizInteractor(dao, presenter);

        GenerateFromWrongInputData input =
                new GenerateFromWrongInputData("test-user", 3);

        // when
        interactor.execute(input);

        // then
        assertFalse(presenter.successCalled, "Success view should not be called.");
        assertTrue(presenter.failCalled, "Fail view should be called.");
        assertNull(dao.lastCreatedQuizId, "DAO should not be asked to create a quiz.");
        assertNotNull(presenter.lastErrorMessage, "Error message should not be null.");
        assertTrue(presenter.lastErrorMessage.contains("No wrong questions"),
                "Error message should mention 'No wrong questions'.");
    }

    /**
     * If player name is null or blank, we should fail fast before hitting the DAO.
     */
    @Test
    void failsWhenPlayerNameIsBlank() {
        TestDAO dao = new TestDAO(new ArrayList<>(), false);
        TestPresenter presenter = new TestPresenter();
        GenerateFromWrongQuizInteractor interactor =
                new GenerateFromWrongQuizInteractor(dao, presenter);

        GenerateFromWrongInputData input =
                new GenerateFromWrongInputData("   ", 3);

        interactor.execute(input);

        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled);
        assertEquals(0, dao.getWrongQuestionsCallCount,
                "DAO should not be called when player name is invalid.");
    }

    /**
     * If requested number is non-positive, we also fail fast.
     */
    @Test
    void failsWhenRequestedNumberIsNonPositive() {
        TestDAO dao = new TestDAO(new ArrayList<>(), false);
        TestPresenter presenter = new TestPresenter();
        GenerateFromWrongQuizInteractor interactor =
                new GenerateFromWrongQuizInteractor(dao, presenter);

        GenerateFromWrongInputData input =
                new GenerateFromWrongInputData("test-user", 0);

        interactor.execute(input);

        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled);
        assertEquals(0, dao.getWrongQuestionsCallCount,
                "DAO should not be called when requested number is invalid.");
    }

    /**
     * If DAO returns null quizId, interactor should treat it as a failure.
     */
    @Test
    void failsWhenDaoReturnsNullQuizId() {
        List<WrongQuestionRecord> wrongQuestions = Arrays.asList(
                new WrongQuestionRecord(
                        "quiz-1",
                        "Quiz 1",
                        "Q1?",
                        Arrays.asList("A", "B", "C", "D"),
                        "A"
                )
        );

        TestDAO dao = new TestDAO(wrongQuestions, true); // configured to return null
        TestPresenter presenter = new TestPresenter();
        GenerateFromWrongQuizInteractor interactor =
                new GenerateFromWrongQuizInteractor(dao, presenter);

        GenerateFromWrongInputData input =
                new GenerateFromWrongInputData("test-user", 1);

        interactor.execute(input);

        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled);
        assertNull(presenter.lastSuccessOutput,
                "There should be no success output when quiz creation fails.");
    }

    // ======================
    // Test doubles (fake DAO & presenter)
    // ======================

    /**
     * In-memory DAO used only for tests.
     * It records how the interactor called it.
     */
    private static class TestDAO implements GenerateFromWrongDataAccessInterface {

        private final List<WrongQuestionRecord> storedWrong;
        private final boolean returnNullQuizId;

        String lastCreatedQuizId;
        List<WrongQuestionRecord> lastCreatedQuestions = new ArrayList<>();
        int getWrongQuestionsCallCount = 0;

        TestDAO(List<WrongQuestionRecord> storedWrong, boolean returnNullQuizId) {
            this.storedWrong = new ArrayList<>(storedWrong);
            this.returnNullQuizId = returnNullQuizId;
        }

        @Override
        public List<WrongQuestionRecord> getWrongQuestionsForPlayer(String playerName) {
            getWrongQuestionsCallCount++;
            return new ArrayList<>(storedWrong);
        }

        @Override
        public String createQuizFromWrongQuestions(
                String playerName,
                List<WrongQuestionRecord> questions) {

            this.lastCreatedQuestions = new ArrayList<>(questions);

            if (returnNullQuizId) {
                this.lastCreatedQuizId = null;
                return null;
            }

            this.lastCreatedQuizId = "practice-" + playerName;
            return lastCreatedQuizId;
        }
    }

    /**
     * Test presenter that just records what was called.
     */
    private static class TestPresenter implements GenerateFromWrongOutputBoundary {

        boolean successCalled = false;
        boolean failCalled = false;

        GenerateFromWrongOutputData lastSuccessOutput;
        String lastErrorMessage;

        @Override
        public void prepareSuccessView(GenerateFromWrongOutputData outputData) {
            this.successCalled = true;
            this.lastSuccessOutput = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failCalled = true;
            this.lastErrorMessage = errorMessage;
        }
    }
}
