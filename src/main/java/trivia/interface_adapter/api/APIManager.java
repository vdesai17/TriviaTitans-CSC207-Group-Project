package trivia.interface_adapter.api;

import com.google.gson.*;
import trivia.entity.Question;
import trivia.use_case.select_quiz.SelectQuizAPIDataAccessInterface;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class APIManager implements SelectQuizAPIDataAccessInterface {

    private final String baseURL = "https://opentdb.com/api.php";

    @Override
    public List<Question> fetchQuestions(String category, String difficulty, int amount) {
        List<Question> questions = new ArrayList<>();
        try {
            String query = String.format("%s?amount=%d&category=%s&difficulty=%s&type=multiple",
                    baseURL, amount,
                    URLEncoder.encode(category, StandardCharsets.UTF_8),
                    URLEncoder.encode(difficulty, StandardCharsets.UTF_8));

            HttpURLConnection conn = (HttpURLConnection) new URL(query).openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != 200)
                throw new IOException("HTTP error: " + conn.getResponseCode());

            InputStreamReader reader =
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
            JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray results = response.getAsJsonArray("results");

            for (JsonElement el : results) {
                JsonObject qObj = el.getAsJsonObject();
                String questionText = qObj.get("question").getAsString();
                String correct = qObj.get("correct_answer").getAsString();

                List<String> options = new ArrayList<>();
                JsonArray incorrect = qObj.getAsJsonArray("incorrect_answers");
                for (JsonElement ans : incorrect)
                    options.add(ans.getAsString());
                options.add(correct);
                Collections.shuffle(options);

                String id = UUID.randomUUID().toString();

                Question q = new Question(id,
                        questionText, options, correct,
                        qObj.get("category").getAsString(),
                        qObj.get("difficulty").getAsString()
                );
                questions.add(q);
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("API fetch failed: " + e.getMessage());
        }
        return questions;
    }
}