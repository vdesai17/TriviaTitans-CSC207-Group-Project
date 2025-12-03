package trivia.use_case.select_quiz;

import trivia.entity.Question;
import java.util.List;

/**
 * Immutable output data object for the "Select Quiz" use case.
 *
 * <p>This class holds the list of {@link Question} entities returned
 * from the data access layer (API) after a successful fetch.
 *
 * <p>The interactor creates an instance of this class and passes it
 * to the Presenter via {@link SelectQuizOutputBoundary#presentSuccess(SelectQuizOutputData)}.
 * The Presenter then transforms it into something the ViewModel/GUI understands.
 */
public class SelectQuizOutputData {

    /** The list of quiz questions that were successfully fetched. */
    private final List<Question> questions;

    /**
     * Constructs an output data object with the given list of questions.
     *
     * @param questions the questions that will be shown to the user
     */
    public SelectQuizOutputData(List<Question> questions) {
        this.questions = questions;
    }

    /**
     * @return the list of questions for the selected quiz
     */
    public List<Question> getQuestions() {
        return questions;
    }
}
