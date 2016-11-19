package berlin.bothack.moodic.services;

import berlin.bothack.moodic.enums.Emotion;
import berlin.bothack.moodic.model.microsoft.cognitive.EmotionResponse;
import org.junit.Test;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
public class MicrosoftCognitiveServiceTests {
    @Test
    public void testTrumpEmotion() {
        MicrosoftCognitiveService microsoftCongitiveService = new MicrosoftCognitiveService();
        EmotionResponse emotionResponse = microsoftCongitiveService.retrieveEmotion("http://www.zerohedge.com/sites/default/files/images/user230519/imageroot/Trump_0.jpg");
        Emotion mostLikableEmotion = microsoftCongitiveService.getMostLikableEmotion(emotionResponse);
        System.out.println(mostLikableEmotion);
        System.out.println(emotionResponse);
        assert Emotion.ANGER.equals(mostLikableEmotion);
    }
}
