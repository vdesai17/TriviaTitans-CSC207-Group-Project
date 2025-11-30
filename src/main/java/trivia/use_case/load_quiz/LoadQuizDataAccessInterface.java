package trivia.use_case.load_quiz;

import trivia.entity.Quiz;
import java.util.List;

/**
 * FIXED: LoadQuizDataAccessInterface - Now complete with all required methods
 * 
 * BEFORE: Only had getQuizzesByPlayer()
 * AFTER: Added getQuizById() and getAllQuizzes()
 * 
 * This interface defines the contract for data access in the Load Quiz use case.
 * Implementations: QuizDataAccessObject
 */
public interface LoadQuizDataAccessInterface {
    
    /**
     * Get all quizzes created by a specific player
     * 
     * @param playerName The name of the player
     * @return List of quizzes created by the player, or empty list if none exist
     */
    List<Quiz> getQuizzesByPlayer(String playerName);
    
    /**
     * Get a quiz by its ID
     * 
     * @param quizId The unique quiz identifier
     * @return The quiz, or null if not found
     */
    Quiz getQuizById(String quizId);
    
    /**
     * Get all quizzes in the system
     * Useful for listing all available quizzes
     * 
     * @return List of all quizzes, or empty list if none exist
     */
    List<Quiz> getAllQuizzes();
}