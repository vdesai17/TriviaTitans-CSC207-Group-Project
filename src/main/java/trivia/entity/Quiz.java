package trivia.entity;

import java.util.ArrayList;
import java.util.List;

public class Quiz {

    private final String id;     // uniquely identifies each quiz
    private String title;
    private String category;
    private String difficulty;
    private String creatorName;
    private List<Question> questions;

    // constructor for creating a new quiz
    public Quiz(String id,
                String title,
                String category,
                String difficulty,
                String creatorName) {

        this.id = id;
        this.title = title;
        this.category = category;
        this.difficulty = difficulty;
        this.creatorName = creatorName;
        this.questions = new ArrayList<>();
    }

    // constructor for loading an exist quiz
    public Quiz(String id,
                String title,
                String category,
                String difficulty,
                String creatorName,
                List<Question> questions) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.difficulty = difficulty;
        this.creatorName = creatorName;
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    // setters
    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public void addQuestion(Question question) {
        questions.add(question);
    }
}
