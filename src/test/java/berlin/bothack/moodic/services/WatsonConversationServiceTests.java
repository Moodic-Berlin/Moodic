package berlin.bothack.moodic.services;

import berlin.bothack.moodic.enums.Emotion;
import org.junit.Test;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
public class WatsonConversationServiceTests {

    @Test
    public void testHappiness() {
        WatsonConversationService service = new WatsonConversationService();
        Emotion emotion = service.retrieveEmotion("Happy");
        assert Emotion.HAPPINESS.equals(emotion);
    }

    @Test
    public void testJoyful() {
        WatsonConversationService service = new WatsonConversationService();
        Emotion emotion = service.retrieveEmotion("Awesome Man!");
        assert Emotion.JOYFUL.equals(emotion);
    }
}
