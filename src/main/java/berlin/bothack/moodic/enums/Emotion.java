package berlin.bothack.moodic.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
public enum Emotion {
    SURPRISE(false),
    JOYFUL(false),
    HAPPINESS(false),
    ROMANTIC(false),
    NEUTRAL(false),
    SLEEPY(false),

    SADNESS(false),
    ANGER(false),
    MAD(false),

    SPORTIVE(false),
    ROCKING(false),
    CLUBBING(false),
    RAP(false),
    REGGAE(false),

    CONTEMPT(true),
    DISGUST(true),
    FEAR(true);

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
