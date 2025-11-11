package trivia.framework.ui;

import trivia.interface_adapter.api.APIManager;
import trivia.entity.Question;
import java.util.List;

public class TestAPI {
    public static void main(String[] args) {
        APIManager api = new APIManager();
        // Category 21 = Sports
        List<Question> questions = api.fetchQuestions("21", "medium", 5);
        for (Question q : questions) {
            System.out.println(q.getQuestionText());
            System.out.println(q.getOptions());
            System.out.println("Correct: " + q.getCorrectAnswer());
            System.out.println("-----");
        }
    }
}
