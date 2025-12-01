package trivia.use_case.load_quiz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import trivia.entity.Quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoadQuizInteractorTest {

    // Fake DAO
    private static class FakeDAO implements LoadQuizDataAccessInterface {

        String receivedPlayer;
        List<Quiz> quizzesToReturn;

        @Override
        public List<Quiz> getQuizzesByPlayer(String playerName) {
            this.receivedPlayer = playerName;
            return quizzesToReturn;
        }

        // Stub method — NOT used in tests, but required
        @Override
        public Quiz getQuizById(String quizId) {
            return null;
        }

        // Stub method — NOT used in tests, but required
        @Override
        public List<Quiz> getAllQuizzes() {
            return null;
        }
    }


    // Fake Presenter
    private static class FakePresenter implements LoadQuizOutputBoundary {
        LoadQuizResponseModel receivedModel;
        boolean presentCalled = false;

        @Override
        public void present(LoadQuizResponseModel responseModel) {
            this.receivedModel = responseModel;
            this.presentCalled = true;
        }
    }

    private FakeDAO fakeDAO;
    private FakePresenter fakePresenter;

    @BeforeEach
    void setup() {
        fakeDAO = new FakeDAO();
        fakePresenter = new FakePresenter();
    }

    // ---------------------------
    // TESTS FOR CONSTRUCTOR
    // ---------------------------

    @Test
    void constructorThrowsWhenDAOIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new LoadQuizInteractor(null, fakePresenter));
    }

    @Test
    void constructorThrowsWhenPresenterIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new LoadQuizInteractor(fakeDAO, null));
    }

    // ---------------------------
    // TEST: null inputData
    // ---------------------------

    @Test
    void executeHandlesNullInputData() {
        LoadQuizInteractor interactor = new LoadQuizInteractor(fakeDAO, fakePresenter);

        interactor.execute(null);

        assertTrue(fakePresenter.presentCalled);
        assertNotNull(fakePresenter.receivedModel);
        assertEquals(Collections.emptyList(), fakePresenter.receivedModel.getQuizzes());
    }

    // ---------------------------
    // TEST: null playerName
    // ---------------------------

    @Test
    void executeHandlesNullPlayerName() {
        LoadQuizInteractor interactor = new LoadQuizInteractor(fakeDAO, fakePresenter);

        LoadQuizInputData inputData = new LoadQuizInputData(null);
        interactor.execute(inputData);

        assertTrue(fakePresenter.presentCalled);
        assertEquals(Collections.emptyList(), fakePresenter.receivedModel.getQuizzes());
    }

    // ---------------------------
    // TEST: DAO returns null
    // ---------------------------

    @Test
    void executeHandlesNullDAOResponse() {
        LoadQuizInteractor interactor = new LoadQuizInteractor(fakeDAO, fakePresenter);

        LoadQuizInputData inputData = new LoadQuizInputData("Vivan");
        fakeDAO.quizzesToReturn = null;

        interactor.execute(inputData);

        assertTrue(fakePresenter.presentCalled);
        assertEquals(Collections.emptyList(), fakePresenter.receivedModel.getQuizzes());
    }

    // ---------------------------
    // TEST: DAO returns quizzes
    // ---------------------------

    @Test
    void executeReturnsQuizzesSuccessfully() {
        LoadQuizInteractor interactor = new LoadQuizInteractor(fakeDAO, fakePresenter);

        LoadQuizInputData inputData = new LoadQuizInputData("Vivan");

        Quiz sampleQuiz = new Quiz(
                "quiz1",
                "Sample Quiz",
                "General Knowledge",
                "easy",
                "Vivan",
                new ArrayList<>()
        );

        List<Quiz> quizList = List.of(sampleQuiz);
        fakeDAO.quizzesToReturn = quizList;

        interactor.execute(inputData);

        assertTrue(fakePresenter.presentCalled);
        assertEquals(quizList, fakePresenter.receivedModel.getQuizzes());
        assertEquals("Vivan", fakeDAO.receivedPlayer);
    }
}
