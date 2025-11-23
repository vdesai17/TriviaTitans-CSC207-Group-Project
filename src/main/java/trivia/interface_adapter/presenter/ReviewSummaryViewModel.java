package trivia.interface_adapter.presenter;

public class ReviewSummaryViewModel {
    private  String score;
    private  String accuracy;

    //Getters for view model

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
