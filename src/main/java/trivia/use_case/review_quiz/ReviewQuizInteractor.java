package trivia.use_case.review_quiz;

import trivia.entity.Question;
import trivia.entity.Quiz;
import trivia.entity.QuizAttempt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReviewQuizInteractor implements ReviewQuizInputBoundary {

    private final ReviewQuizAttemptDataAccessInterface attemptDataAccess;
    private final ReviewQuizQuizDataAccessInterface quizDataAccess;
    private final ReviewQuizOutputBoundary presenter;

    public ReviewQuizInteractor(ReviewQuizAttemptDataAccessInterface attemptDataAccess,
                                ReviewQuizQuizDataAccessInterface quizDataAccess,
                                ReviewQuizOutputBoundary presenter) {
        this.attemptDataAccess = attemptDataAccess;
        this.quizDataAccess = quizDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void viewPastQuizzes(String playerName) {
        List<QuizAttempt> attempts = attemptDataAccess.getAttemptsForPlayer(playerName);
        ReviewQuizResponseModel response = new ReviewQuizResponseModel();

        if (attempts.isEmpty()) {
            response.setMessage("No past quizzes found.");
            response.setPastQuizzes(new ArrayList<>());
        } else {
            List<ReviewQuizResponseModel.PastQuizSummary> summaries = new ArrayList<>();
            for (QuizAttempt attempt : attempts) {
                Quiz quiz = quizDataAccess.getQuizById(attempt.getQuizId());
                summaries.add(new ReviewQuizResponseModel.PastQuizSummary(
                        attempt.getAttemptId(),
                        quiz.getTitle(),
                        attempt.getScore(),
                        attempt.getCompletedAt()
                ));
            }
            response.setPastQuizzes(summaries);
        }

        presenter.presentPastQuizList(response);
    }

    @Override
    public void openAttempt(String attemptId) {
        Optional<QuizAttempt> maybeAttempt = attemptDataAccess.getAttemptById(attemptId);
        ReviewQuizResponseModel response = new ReviewQuizResponseModel();

        if (!maybeAttempt.isPresent()) {
            response.setMessage("Quiz attempt not found.");
            presenter.presentQuizAttempt(response);
            return;
        }

        QuizAttempt attempt = maybeAttempt.get();
        Quiz quiz = quizDataAccess.getQuizById(attempt.getQuizId());

        List<Question> questions = quiz.getQuestions();
        List<Integer> selected = attempt.getSelectedOptionIndices();
        if (selected == null) {
            selected = new ArrayList<>();
        }

        List<ReviewQuizResponseModel.QuestionRow> rows = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int selectedIndex = (i < selected.size()) ? selected.get(i) : -1;
            rows.add(new ReviewQuizResponseModel.QuestionRow(
                    q.getQuestionText(),
                    q.getOptions(),
                    q.getCorrectOptionIndex(),
                    selectedIndex
            ));
        }

        response.setAttemptId(attempt.getAttemptId());
        response.setQuizTitle(quiz.getTitle());
        response.setQuestions(rows);
        response.setEditingEnabled(attempt.isEditable());
        presenter.presentQuizAttempt(response);
    }

    @Override
    public void saveEditedAnswers(ReviewQuizRequestModel requestModel) {
        Optional<QuizAttempt> maybeAttempt = attemptDataAccess.getAttemptById(requestModel.getAttemptId());
        ReviewQuizResponseModel response = new ReviewQuizResponseModel();

        if (!maybeAttempt.isPresent()) {
            response.setMessage("Quiz attempt not found.");
            presenter.presentSaveResult(response);
            return;
        }

        QuizAttempt attempt = maybeAttempt.get();

        if (!attempt.isEditable()) {
            response.setMessage("Editing is restricted.");
            response.setEditingEnabled(false);
            presenter.presentSaveResult(response);
            return;
        }

        // update answers & recompute score
        attempt.setSelectedOptionIndices(requestModel.getUpdatedSelectedOptionIndices());

        Quiz quiz = quizDataAccess.getQuizById(attempt.getQuizId());
        int newScore = 0;
        List<Question> questions = quiz.getQuestions();
        List<Integer> updated = requestModel.getUpdatedSelectedOptionIndices();

        for (int i = 0; i < questions.size() && i < updated.size(); i++) {
            if (questions.get(i).getCorrectOptionIndex() == updated.get(i)) {
                newScore++;
            }
        }
        attempt.setScore(newScore);

        attemptDataAccess.updateAttempt(attempt);

        response.setAttemptId(attempt.getAttemptId());
        response.setEditingEnabled(true);
        response.setMessage("Changes saved.");
        presenter.presentSaveResult(response);
    }
}
