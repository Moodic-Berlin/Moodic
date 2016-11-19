package berlin.bothack.moodic.model.microsoft.cognitive;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
public class EmotionResponse {
    Scores scores;

    public Scores getScores() {
        return scores;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "EmotionResponse{" +
                "scores=" + scores +
                '}';
    }
}
