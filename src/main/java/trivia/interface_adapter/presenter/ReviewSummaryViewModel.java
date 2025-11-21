package trivia.interface_adapter.presenter;

public class ReviewSummaryViewModel {
    private  String quizTitle;
    private  String quizId;
    private  String score;
    private  String accuracy;

    public ReviewSummaryViewModel(String quizTitle, String quizId, String score, String accuracy) {
        this.quizTitle = quizTitle;
        this.quizId = quizId;
        this.score = score;
        this.accuracy = accuracy;
    }

    //Getters for view model
    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }
}
