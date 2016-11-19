package berlin.bothack.moodic.services;

import berlin.bothack.moodic.enums.Emotion;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
public class WatsonConversationServiceTests {

    @Test
    public void testHappiness() {
        WatsonConversationService service = new WatsonConversationService();
        Emotion emotion = service.retrieveEmotion("Happy");
        Assert.assertEquals(Emotion.HAPPINESS, emotion);
    }

    @Test
    public void testJoyful() {
        WatsonConversationService service = new WatsonConversationService();
        Emotion emotion = service.retrieveEmotion("Awesome Man!");
        Assert.assertEquals(Emotion.JOYFUL, emotion);
    }

    @Test
    public void testFuck() {
        WatsonConversationService service = new WatsonConversationService();
        Emotion emotion = service.retrieveEmotion("I feel fuck!");
        Assert.assertEquals(Emotion.NEUTRAL, emotion);
        emotion = service.retrieveEmotion("Fuck you!");
        Assert.assertEquals(Emotion.MAD, emotion);
    }

    @Test
    public void testKill() {
        WatsonConversationService service = new WatsonConversationService();
        Emotion emotion = service.retrieveEmotion("I will kill him!");
        Assert.assertEquals(Emotion.MAD, emotion);
    }

    @Test
    public void test4() {
        WatsonConversationService service = new WatsonConversationService();
        Emotion emotion = service.retrieveEmotion("Life surprise confuse interested");
        Assert.assertEquals(Emotion.HAPPINESS, emotion);
    }
}
