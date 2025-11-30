package trivia.interface_adapter.presenter;

import trivia.use_case.review_quiz.ReviewQuizOutputBoundary;
import trivia.use_case.review_quiz.ReviewQuizResponseModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for Use Case 3: Review & Edit Past Questions
 * Now includes redo quiz presentation following Clean Architecture
 * Converts use case response models into ViewModel format for UI
 */
public class PastQuizPresenter implements ReviewQuizOutputBoundary {

    private final PastQuizViewModel viewModel;
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PastQuizPresenter(PastQuizViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentPastQuizList(ReviewQuizResponseModel responseModel) {
        List<ReviewQuizResponseModel.PastQuizSummary> summaries =
                responseModel.getPastQuizzes();

        if (summaries == null || summaries.isEmpty()) {
            viewModel.setPastQuizzes(new ArrayList<>());
            viewModel.setMessage(responseModel.getMessage() != null ?
                    responseModel.getMessage() : "No past quizzes found.");
        } else {
            List<PastQuizViewModel.PastQuizSummaryViewModel> viewModelList =
                    new ArrayList<>();

            for (ReviewQuizResponseModel.PastQuizSummary summary : summaries) {
                String raw = summary.getCompletedAt();
                String formattedDate = raw;

                if (raw != null && !raw.isEmpty()) {
                    try {
                        LocalDateTime dt = LocalDateTime.parse(raw);
                        formattedDate = dt.format(DATE_FORMATTER);
                    } catch (DateTimeParseException e) {
                        formattedDate = raw;
                    }
                }

                viewModelList.add(new PastQuizViewModel.PastQuizSummaryViewModel(
                        summary.getAttemptId(),
                        summary.getQuizTitle(),
                        summary.getScore(),
                        formattedDate
                ));
            }

            viewModel.setPastQuizzes(viewModelList);
            viewModel.setMessage("");
        }
    }

    @Override
    public void presentQuizAttempt(ReviewQuizResponseModel responseModel) {
        if (responseModel.getAttemptId() == null) {
            viewModel.setMessage(responseModel.getMessage());
            viewModel.setQuestions(new ArrayList<>());
            return;
        }

        viewModel.setCurrentAttemptId(responseModel.getAttemptId());
        viewModel.setQuizTitle(responseModel.getQuizTitle());
        viewModel.setEditingEnabled(responseModel.isEditingEnabled());

        List<ReviewQuizResponseModel.QuestionRow> questionRows = 
                responseModel.getQuestions();

        if (questionRows != null) {
            List<PastQuizViewModel.QuestionRowViewModel> viewModelQuestions = 
                    new ArrayList<>();

            for (ReviewQuizResponseModel.QuestionRow row : questionRows) {
                viewModelQuestions.add(new PastQuizViewModel.QuestionRowViewModel(
                        row.getQuestionText(),
                        row.getOptions(),
                        row.getCorrectIndex(),
                        row.getSelectedIndex()
                ));
            }

            viewModel.setQuestions(viewModelQuestions);
        }

        String message = responseModel.getMessage();
        viewModel.setMessage(message != null ? message : "");
    }

    @Override
    public void presentSaveResult(ReviewQuizResponseModel responseModel) {
        String message = responseModel.getMessage();
        
        if (message != null) {
            viewModel.setMessage(message);
        }

        // If editing is now disabled, reflect that in the view
        viewModel.setEditingEnabled(responseModel.isEditingEnabled());
    }

    /**
     * ✅ NEW: Present redo quiz to the ViewModel
     * This triggers the UI to navigate to the quiz screen
     */
    @Override
    public void presentRedoQuiz(ReviewQuizResponseModel responseModel) {
        if (responseModel.getQuizToRedo() != null) {
            // Set the quiz in the ViewModel - UI will observe this change
            viewModel.setQuizToRedo(responseModel.getQuizToRedo());
            viewModel.setMessage(""); // Clear any error messages
        } else {
            // Error case - quiz not found or has no questions
            viewModel.setQuizToRedo(null);
            viewModel.setMessage(responseModel.getMessage());
        }
    }
}