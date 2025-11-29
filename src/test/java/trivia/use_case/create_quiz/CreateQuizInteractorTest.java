package trivia.use_case.create_quiz;

import org.junit.jupiter.api.Test;
import trivia.entity.Question;
import trivia.entity.Quiz;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateQuizInteractorTest {

    // ---------- Stub DAO ----------
    private static class InMemoryQuizDAO implements CreateQuizDataAccessInterface {
        Quiz savedQuiz;
        boolean existsReturn;

        InMemoryQuizDAO(boolean existsReturn) {
            this.existsReturn = existsReturn;
        }

        @Override
        public boolean existsById(String id) {
            return existsReturn;
        }

        @Override
        public void save(Quiz quiz) {
            this.savedQuiz = quiz;
        }
    }

    // ---------- Stub Presenter ----------
    private static class TestPresenter implements CreateQuizOutputBoundary {
        String failMessage;
        CreateQuizOutputData successData;

        @Override
        public void prepareFailView(String errorMessage) {
            this.failMessage = errorMessage;
        }

        @Override
        public void prepareSuccessView(CreateQuizOutputData outputData) {
            this.successData = outputData;
        }
    }

    // ---------- create question----------
    private AddQuestionInputData validQuestion() {
        return new AddQuestionInputData(
                "dummyQuizId",
                "What is 1+1?",
                List.of("2", "3"),
                "2",
                "Math",
                "easy"
        );
    }

    // ---------- create quiz ----------
    private CreateQuizInputData validQuiz(List<AddQuestionInputData> qs) {
        return new CreateQuizInputData(
                "My Quiz",
                "General",
                "medium",
                "Alice",
                qs
        );
    }

    // ===============================================================
    //  FAIL CASES
    // ===============================================================

    @Test
    void fail_titleBlank() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        CreateQuizInputData input =
                new CreateQuizInputData("   ", "General", "medium", "Alice", List.of(validQuestion()));

        interactor.execute(input);

        assertEquals("Quiz title cannot be empty.", presenter.failMessage);
        assertNull(dao.savedQuiz);
    }

    @Test
    void fail_creatorBlank() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        CreateQuizInputData input =
                new CreateQuizInputData("My Quiz", "General", "medium", "   ", List.of(validQuestion()));

        interactor.execute(input);

        assertEquals("Creator name cannot be empty.", presenter.failMessage);
        assertNull(dao.savedQuiz);
    }

    @Test
    void fail_questionsNull() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        CreateQuizInputData input =
                new CreateQuizInputData("My Quiz", "General", "medium", "Alice", null);

        interactor.execute(input);

        assertEquals("Quiz must contain at least one question.", presenter.failMessage);
    }

    @Test
    void fail_questionTextBlank() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        AddQuestionInputData badQ = new AddQuestionInputData(
                "quizid", "   ", List.of("A", "B"), "A", "Math", "easy"
        );

        interactor.execute(validQuiz(List.of(badQ)));

        assertEquals("Question text cannot be empty.", presenter.failMessage);
    }

    @Test
    void fail_optionsNull() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        AddQuestionInputData badQ = new AddQuestionInputData(
                "quizid", "Q?", null, "A", "Math", "easy"
        );

        interactor.execute(validQuiz(List.of(badQ)));

        assertEquals("Each question must have at least two options.", presenter.failMessage);
    }

    @Test
    void fail_correctAnswerBlank() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        AddQuestionInputData badQ = new AddQuestionInputData(
                "quizid", "Q?", List.of("A", "B"), "   ", "Math", "easy"
        );

        interactor.execute(validQuiz(List.of(badQ)));

        assertEquals("Correct answer cannot be empty.", presenter.failMessage);
    }

    @Test
    void fail_correctAnswerNotInOptions() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        AddQuestionInputData badQ = new AddQuestionInputData(
                "quizid", "Q?", List.of("A", "B"), "C", "Math", "easy"
        );

        interactor.execute(validQuiz(List.of(badQ)));

        assertEquals("Correct answer must be one of the options.", presenter.failMessage);
    }

    // ===============================================================
    //  SUCCESS CASE 1：question using quiz  difficulty
    // ===============================================================

    @Test
    void success_questionInheritQuizCategoryDifficulty_whenMissing() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false); // existsById → false, so isSuccess = true
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        // category = "" & difficulty = null → 用 quiz 的
        AddQuestionInputData q = new AddQuestionInputData(
                "quizid", "Q?", List.of("A", "B"), "A", "", null
        );

        CreateQuizInputData input =
                new CreateQuizInputData("My Quiz", "QuizCat", "hard", "Alice", List.of(q));

        interactor.execute(input);

        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertNotNull(dao.savedQuiz);

        // outputData
        CreateQuizOutputData out = presenter.successData;
        assertEquals(dao.savedQuiz.getId(), out.getQuizId());
        assertEquals("My Quiz", out.getTitle());
        assertEquals("QuizCat", out.getCategory());
        assertEquals("hard", out.getDifficulty());
        assertEquals(1, out.getQuestionCount());
        assertTrue(out.isSuccess());   // !false = true

        // question inherited quiz category/difficulty
        Question savedQ = dao.savedQuiz.getQuestions().get(0);
        assertEquals("QuizCat", savedQ.getCategory());
        assertEquals("hard", savedQ.getDifficulty());
    }

    // ===============================================================
    //  SUCCESS CASE 2：quiz it self category/difficulty
    // ===============================================================

    @Test
    void success_questionUsesOwnCategoryDifficulty_whenProvided() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(true); // existsById → true, so isSuccess = false
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        AddQuestionInputData q = new AddQuestionInputData(
                "quizid", "Q?", List.of("A", "B"), "A",
                "QCat", "easy"
        );

        CreateQuizInputData input =
                new CreateQuizInputData("My Quiz", "QuizCat", "hard", "Alice", List.of(q));

        interactor.execute(input);

        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertNotNull(dao.savedQuiz);

        // existsById = true → isSuccess = !true = false
        assertFalse(presenter.successData.isSuccess());

        // question should keep its own values
        Question savedQ = dao.savedQuiz.getQuestions().get(0);
        assertEquals("QCat", savedQ.getCategory());
        assertEquals("easy", savedQ.getDifficulty());
    }
    @Test
    void fail_titleNull() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        CreateQuizInputData input =
                new CreateQuizInputData(null, "General", "medium", "Alice",
                        List.of(validQuestion()));

        interactor.execute(input);

        assertEquals("Quiz title cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
        assertNull(dao.savedQuiz);
    }

    @Test
    void fail_creatorNull() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        CreateQuizInputData input =
                new CreateQuizInputData("My Quiz", "General", "medium", null,
                        List.of(validQuestion()));

        interactor.execute(input);

        assertEquals("Creator name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
        assertNull(dao.savedQuiz);
    }

    @Test
    void fail_questionsEmptyList() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        CreateQuizInputData input =
                new CreateQuizInputData("My Quiz", "General", "medium", "Alice",
                        List.of()  // empty list
                );

        interactor.execute(input);

        assertEquals("Quiz must contain at least one question.", presenter.failMessage);
        assertNull(presenter.successData);
        assertNull(dao.savedQuiz);
    }

    @Test
    void fail_questionTextNull() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        AddQuestionInputData badQ = new AddQuestionInputData(
                "quizid",
                null,                        // questionText == null
                List.of("A", "B"),
                "A",
                "Math",
                "easy"
        );

        interactor.execute(validQuiz(List.of(badQ)));

        assertEquals("Question text cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
        assertNull(dao.savedQuiz);
    }

    @Test
    void fail_optionsTooFew() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        AddQuestionInputData badQ = new AddQuestionInputData(
                "quizid",
                "Q?",
                List.of("A"),                 // test for one option
                "A",
                "Math",
                "easy"
        );

        interactor.execute(validQuiz(List.of(badQ)));

        assertEquals("Each question must have at least two options.", presenter.failMessage);
        assertNull(presenter.successData);
        assertNull(dao.savedQuiz);
    }

    @Test
    void fail_correctAnswerNull() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        AddQuestionInputData badQ = new AddQuestionInputData(
                "quizid",
                "Q?",
                List.of("A", "B"),
                null,                         // correctAnswer == null
                "Math",
                "easy"
        );

        interactor.execute(validQuiz(List.of(badQ)));

        assertEquals("Correct answer cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
        assertNull(dao.savedQuiz);
    }

    @Test
    void success_questionCategoryNull_usesQuizCategory() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        // category = null
        AddQuestionInputData q = new AddQuestionInputData(
                "quizid",
                "Q?",
                List.of("A", "B"),
                "A",
                null,          // category == null
                "easy"        // difficulty
        );

        CreateQuizInputData input =
                new CreateQuizInputData("My Quiz", "QuizCat", "hard", "Alice", List.of(q));

        interactor.execute(input);

        assertNull(presenter.failMessage);
        assertNotNull(dao.savedQuiz);

        Question savedQ = dao.savedQuiz.getQuestions().get(0);
        assertEquals("QuizCat", savedQ.getCategory());
        assertEquals("easy", savedQ.getDifficulty());
    }

    @Test
    void success_questionDifficultyEmpty_usesQuizDifficulty() {
        InMemoryQuizDAO dao = new InMemoryQuizDAO(false);
        TestPresenter presenter = new TestPresenter();
        CreateQuizInteractor interactor = new CreateQuizInteractor(dao, presenter);

        // difficulty = ""，会走 (difficulty.isEmpty()) 这一支
        AddQuestionInputData q = new AddQuestionInputData(
                "quizid",
                "Q?",
                List.of("A", "B"),
                "A",
                "QCat",        // has category
                ""             // null difficulty
        );

        CreateQuizInputData input =
                new CreateQuizInputData("My Quiz", "QuizCat", "hard", "Alice", List.of(q));

        interactor.execute(input);

        assertNull(presenter.failMessage);
        assertNotNull(dao.savedQuiz);

        Question savedQ = dao.savedQuiz.getQuestions().get(0);
        assertEquals("QCat", savedQ.getCategory());      // has  category
        assertEquals("hard", savedQ.getDifficulty());    // quiz difficulty
    }

}
