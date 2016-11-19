package berlin.bothack.moodic.services;

import berlin.bothack.moodic.enums.Emotion;
import org.junit.Test;

public class LogicServiceTests {
    @Test
    public void test1() {
        LogicService logicService = new LogicService();
        System.out.println(logicService.emotionToGenre(Emotion.ANGER));
        System.out.println(logicService.emotionToGenre(Emotion.HAPPINESS));
        System.out.println(logicService.emotionToGenre(Emotion.SLEEPY));
    }
}
