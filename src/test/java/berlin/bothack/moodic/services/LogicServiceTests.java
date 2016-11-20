package berlin.bothack.moodic.services;

import berlin.bothack.moodic.enums.Emotion;
import org.junit.Test;

public class LogicServiceTests {
    @Test
    public void test1() {
        EmotionAnalysisService emotionAnalysisService = new EmotionAnalysisService();
        System.out.println(emotionAnalysisService.emotionToGenre(Emotion.ANGER));
        System.out.println(emotionAnalysisService.emotionToGenre(Emotion.HAPPINESS));
        System.out.println(emotionAnalysisService.emotionToGenre(Emotion.SLEEPY));
    }
}
