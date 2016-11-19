package berlin.bothack.moodic.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
public enum Emotion {
    CLUBBING(false),
    JOYFUL(false),
    MAD(false),
    RAP(false),
    REGGAE(false),
    ROCKING(false),
    ROMANTIC(false),
    SLEEPY(false),
    SPORTIVE(false),
    ANGER(false),
    CONTEMPT(true),
    DISGUST(true),
    FEAR(true),
    HAPPINESS(false),
    NEUTRAL(false),
    SADNESS(false),
    SURPRISE(false);

    public final boolean isFaceExclusiveEmotion;

    Emotion(boolean isFaceExclusiveEmotion) {
        this.isFaceExclusiveEmotion = isFaceExclusiveEmotion;
    }

    public static List<String> listEmotionsNoExclusiveFace() {
        return Arrays.stream(Emotion.values())
                .filter(it -> !it.isFaceExclusiveEmotion)
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
