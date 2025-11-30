package trivia.framework;

import trivia.interface_adapter.controller.*;
import trivia.interface_adapter.dao.PlayerDataAccessObject;
import trivia.interface_adapter.dao.QuizDataAccessObject;
import trivia.interface_adapter.presenter.*;
import trivia.use_case.complete_quiz.CompleteQuizInteractor;
import trivia.use_case.complete_quiz.CompleteQuizOutputBoundary;
import trivia.use_case.create_quiz.CreateQuizInteractor;
import trivia.use_case.generate_from_wrong.GenerateFromWrongDataAccessInterface;
import trivia.use_case.generate_from_wrong.GenerateFromWrongQuizInteractor;
import trivia.use_case.load_quiz.LoadQuizInteractor;
import trivia.use_case.load_quiz.LoadQuizDataAccessInterface;
import trivia.use_case.review_quiz.ReviewQuizInteractor;
import trivia.use_case.register_player.RegisterPlayerInteractor;
import trivia.use_case.review_summary.ReviewSummaryInteractor;
import trivia.use_case.select_quiz.SelectQuizInteractor;
import trivia.interface_adapter.api.APIManager;

/**
 * AppFactory — centralized dependency injection container.
 * 
 * This factory creates all DAOs, Interactors, Presenters, and Controllers.
 * UI screens should NEVER instantiate these directly; they should only receive
 * them through their constructors from this factory.
 * 
 * FIXED: SelectQuizController now properly includes presenter and viewmodel
 * 
 * This ensures clean architecture by keeping all dependencies in one place.
 */
public class AppFactory {
    
    // Singleton instances to avoid creating multiple DAOs
    private static final QuizDataAccessObject quizDAO = new QuizDataAccessObject();
    private static final PlayerDataAccessObject playerDAO = new PlayerDataAccessObject();
    private static final APIManager apiManager = new APIManager();
    
    // Singleton ViewModels to maintain state across screens
    private static final CreateQuizViewModel createQuizViewModel = new CreateQuizViewModel();
    private static final LoadQuizViewModel loadQuizViewModel = new LoadQuizViewModel();
    private static final PastQuizViewModel pastQuizViewModel = new PastQuizViewModel();
    private static final GenerateFromWrongViewModel generateFromWrongViewModel = new GenerateFromWrongViewModel();
    private static final ReviewSummaryViewModel reviewSummaryViewModel = new ReviewSummaryViewModel();
    private static final SelectQuizViewModel selectQuizViewModel = new SelectQuizViewModel();  // ✅ NEW
    
    // --- DAO Access ---
    public static QuizDataAccessObject getQuizDAO() {
        return quizDAO;
    }
    
    public static PlayerDataAccessObject getPlayerDAO() {
        return playerDAO;
    }
    
    // --- Select Quiz (Use Case: Select API Quiz) ---
    /**
     * FIXED: Now creates SelectQuizController with proper dependencies
     * 
     * Dependencies: Interactor, Presenter, ViewModel
     * Flow: Controller injects into Screen → Screen listens to ViewModel changes
     */
    public static SelectQuizController createSelectQuizController() {
        SelectQuizPresenter presenter = new SelectQuizPresenter(selectQuizViewModel);
        SelectQuizInteractor interactor = new SelectQuizInteractor(apiManager, presenter);
        return new SelectQuizController(interactor, presenter, selectQuizViewModel);
    }
    
    public static SelectQuizViewModel createSelectQuizViewModel() {
        return selectQuizViewModel;
    }
    
    // --- Create Quiz (Use Case: Create Custom Quiz) ---
    public static CreateQuizController createCreateQuizController() {
        CreateQuizPresenter presenter = new CreateQuizPresenter(createQuizViewModel);
        CreateQuizInteractor interactor = new CreateQuizInteractor(quizDAO, presenter);
        return new CreateQuizController(interactor);
    }
    
    public static CreateQuizViewModel createCreateQuizViewModel() {
        return createQuizViewModel;
    }
    
    // --- Load Quiz (Use Case: Load Custom Quiz) ---
    public static LoadQuizController createLoadQuizController() {
        LoadQuizPresenter presenter = new LoadQuizPresenter();
        LoadQuizDataAccessInterface dataAccess = quizDAO;
        LoadQuizInteractor interactor = new LoadQuizInteractor(dataAccess, presenter);
        return new LoadQuizController(interactor, presenter);
    }
    
    public static LoadQuizViewModel createLoadQuizViewModel() {
        return loadQuizViewModel;
    }
    
    // --- Complete Quiz (Use Case: Complete Quiz) ---
    public static CompleteQuizController createCompleteQuizController() {
        CompleteQuizOutputBoundary presenter = new CompleteQuizOutputBoundary() {
            @Override
            public void present(trivia.use_case.complete_quiz.CompleteQuizOutputData data) {
                // Silent presenter - just records the data
            }
        };
        CompleteQuizInteractor interactor = new CompleteQuizInteractor(quizDAO, presenter);
        return new CompleteQuizController(interactor);
    }
    
    // --- Review Quiz (Use Case: Review Past Quizzes) ---
    public static ReviewController createReviewController() {
        PastQuizPresenter presenter = new PastQuizPresenter(pastQuizViewModel);
        ReviewQuizInteractor interactor = new ReviewQuizInteractor(playerDAO, playerDAO, presenter);
        return new ReviewController(interactor);
    }
    
    public static PastQuizViewModel createPastQuizViewModel() {
        return pastQuizViewModel;
    }
    
    public static PastQuizPresenter createPastQuizPresenter(PastQuizViewModel viewModel) {
        return new PastQuizPresenter(viewModel);
    }
    
    // --- Generate From Wrong (Use Case: Practice Wrong Questions) ---
    public static GenerateFromWrongController createGenerateFromWrongController() {
        GenerateFromWrongPresenter presenter = new GenerateFromWrongPresenter(generateFromWrongViewModel);
        GenerateFromWrongDataAccessInterface dataAccess = quizDAO;
        GenerateFromWrongQuizInteractor interactor = 
                new GenerateFromWrongQuizInteractor(dataAccess, presenter);
        return new GenerateFromWrongController(interactor);
    }
    
    public static GenerateFromWrongViewModel createGenerateFromWrongViewModel() {
        return generateFromWrongViewModel;
    }
    
    // --- Review Summary (Use Case: Display Quiz Summary) ---
    public static ReviewSummaryViewModel createReviewSummaryViewModel() {
        return reviewSummaryViewModel;
    }
    
    public static ReviewSummaryPresenter createReviewSummaryPresenter(ReviewSummaryViewModel viewModel) {
        return new ReviewSummaryPresenter(viewModel);
    }
    
    public static ReviewSummaryController createReviewSummaryController() {
        ReviewSummaryPresenter presenter = new ReviewSummaryPresenter(reviewSummaryViewModel);
        ReviewSummaryInteractor interactor = new ReviewSummaryInteractor(presenter);
        return new ReviewSummaryController(interactor);
    }
    
    // --- Register Player (Use Case: Login/Register) ---
    public static PlayerController createPlayerController() {
        RegisterPlayerInteractor interactor = new RegisterPlayerInteractor(playerDAO);
        return new PlayerController(interactor);
    }
}