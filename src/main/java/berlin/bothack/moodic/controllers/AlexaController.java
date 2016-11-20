package berlin.bothack.moodic.controllers;

import berlin.bothack.moodic.enums.Emotion;
import berlin.bothack.moodic.services.EmotionAnalysisService;
import berlin.bothack.moodic.services.SpotifyService;
import berlin.bothack.moodic.services.WatsonConversationService;
import com.wrapper.spotify.models.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlexaController {
    private static final Logger log = LoggerFactory.getLogger(AlexaController.class);

    private final EmotionAnalysisService emotionAnalysisService;
    private final WatsonConversationService watsonConversationService;
    private final SpotifyService spotifyService;

    public AlexaController(EmotionAnalysisService emotionAnalysisService, WatsonConversationService watsonConversationService, SpotifyService spotifyService) {
        this.emotionAnalysisService = emotionAnalysisService;
        this.watsonConversationService = watsonConversationService;
        this.spotifyService = spotifyService;
    }

    @RequestMapping("/alexa")
    public String process(String text) {
        log.info("ALEXA TEXT: " + text);
        Emotion emotion = watsonConversationService.retrieveEmotion(text);
        String genre = emotionAnalysisService.emotionToGenre(emotion.name());
        Track track = spotifyService.randomTrackForGenre(genre);
        String res = emotion.name() + "\n" + track.getName() + " by " + track.getArtists().get(0).getName();
        log.info("ALEXA REPLY: " + res);
        return res;
    }
}
