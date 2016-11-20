package berlin.bothack.moodic.controllers;

import berlin.bothack.moodic.Conf;
import berlin.bothack.moodic.enums.Emotion;
import berlin.bothack.moodic.model.fb.MessageSender;
import berlin.bothack.moodic.model.fb.QuickReplyBuilder;
import berlin.bothack.moodic.model.fb.json.*;
import berlin.bothack.moodic.model.microsoft.cognitive.EmotionResponse;
import berlin.bothack.moodic.services.EmotionAnalysisService;
import berlin.bothack.moodic.services.MicrosoftCognitiveService;
import berlin.bothack.moodic.services.SpotifyService;
import berlin.bothack.moodic.services.WatsonConversationService;
import berlin.bothack.moodic.util.Messages;
import com.wrapper.spotify.models.Track;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * @author vgorin
 *         file created on 11/19/16 1:36 PM
 */


@RestController
public class FacebookController {
    public static final String COOL_I_WANT_MORE = "Cool, I want more";
    public static final String ANOTHER_ONE_PLEASE = "Another one, please";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final SpotifyService spotifyService;
    private final Conf conf;
    private final MessageSender messageSender;
    private final Messages messages;
    private final MicrosoftCognitiveService microsoftCognitiveService;
    private final WatsonConversationService watsonConversationService;
    private final EmotionAnalysisService emotionAnalysisService;

    @Autowired
    public FacebookController(
            Messages messages,
            SpotifyService spotifyService,
            Conf conf,
            MessageSender messageSender,
            MicrosoftCognitiveService microsoftCognitiveService,
            WatsonConversationService watsonConversationService,
            EmotionAnalysisService emotionAnalysisService
    ) {
        this.spotifyService = spotifyService;
        this.conf = conf;
        this.messageSender = messageSender;
        this.messages = messages;
        this.microsoftCognitiveService = microsoftCognitiveService;
        this.watsonConversationService = watsonConversationService;
        this.emotionAnalysisService = emotionAnalysisService;
    }

    @RequestMapping(
            value = "/spotifyWebhook",
            method = RequestMethod.GET,
            headers = "Accept=plain/text"
    )
    public String getSpotifyWebHook() {
        return "Hey";
    }

    @RequestMapping(
            value = "/webhook",
            method = RequestMethod.GET,
            params = {"hub.mode", "hub.challenge", "hub.verify_token"},
            headers = "Accept=text/plain",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String processWebHook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token
    ) {
        log.info("webhook received, mode = {}, challenge = {}, token = {}", mode, challenge, token);
        if (conf.FB_VERIFY_TOKEN.equals(token)) {
            log.info("webhook verify ok, token = {}", token);
            return challenge;
        }
        log.warn("webhook verify failed, token = {}", token);
        return String.format("wrong token! challenge = %s, token = %s", challenge, token);
    }

    @RequestMapping(
            value = "/webhook",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public String postWebHook(@RequestBody Callback callback) throws Exception {
        log.info("callback received, {}", callback);
        for (Entry entry : callback.entry) {
            for (Messaging messaging : entry.messaging) {
                String senderId = messaging.sender.id;
                try {
                    String imageUrl = getImageUrl(messaging);
                    String emotion = getQuickReply(messaging);
                    String postback = getPostback(messaging);
                    String genre = null;
                    if (postback != null) {
                        String[] parts = postback.split("/");
                        emotion = parts[0];
                        genre = parts[1];
                    }
                    String text = getTextMessage(messaging);

                    int offset = 0;
                    if (emotion != null && emotion.startsWith("+"))
                        offset = Integer.parseInt(emotion);
                    else {
                        if (imageUrl != null) {
                            processImage(senderId, imageUrl);
                        } else if (genre != null) {
                            processGenre(senderId, emotion, genre);
                        } else if (emotion != null) {
                            processEmotion(senderId, emotion, false);
                        } else if (text != null) {
                            processTextMessage(senderId, text);
                        } else {
                            log.info("unknown action for {}", senderId);
                        }
                    }
                    sendFooterQuickReply(senderId, offset, 7);
                } catch (Exception ex) {
                    messageSender.send(senderId, "Ups, we've got an error \uD83D\uDE1E: " + ex);
                    log.error("", ex);
                }
            }
        }
        return "";
    }


    private String getImageUrl(Messaging messaging) {
        if (messaging.message != null && messaging.message.attachments != null && messaging.message.attachments.size() > 0) {
            Payload payload = messaging.message.attachments.iterator().next().payload;
            if (payload != null && payload.url != null) {
                return payload.url;
            }
        }
        return null;
    }

    private String getQuickReply(Messaging messaging) {
        if (messaging.message != null && messaging.message.quickReply != null && messaging.message.quickReply.payload != null)
            return messaging.message.quickReply.payload;
        return null;
    }

    private String getPostback(Messaging messaging) {
        if (messaging.postback != null)
            return messaging.postback.payload;
        return null;
    }

    private String getTextMessage(Messaging messaging) {
        if (messaging.message != null && messaging.message.text != null) {
            return messaging.message.text;
        }
        return null;
    }

    private Response processTextMessage(String senderId, String text) throws IOException {
        try {
            Emotion emotion = watsonConversationService.retrieveEmotion(text);
            log.info("text {} converted emotion {}", text, emotion);
            return processEmotion(senderId, emotion.name(), true);
        } catch (Exception ex) {
            log.warn("error processing text to emotion/genre: {}", text);
            return sendNoEmotion(senderId);
        }
    }

    private Response processImage(String senderId, String imageUrl) throws IOException {
        try {
            EmotionResponse emotionResponse = microsoftCognitiveService.retrieveEmotion(imageUrl);
            log.info("Emotion Response is {}", emotionResponse);
            Emotion emotion = microsoftCognitiveService.getMostLikableEmotion(emotionResponse);
            return processEmotion(senderId, emotion.name(), true);
        } catch (Exception ex) {
            log.warn("error processing image to emotion/genre: {}", imageUrl);
            return sendNoEmotion(senderId);
        }
    }

    private Response processEmotion(String senderId, String emotion, boolean sendEmotion) throws IOException {
        String genre = emotionAnalysisService.emotionToGenre(emotion);
        log.info("Received emotion: {}, corresponding genre is: {}", emotion, genre);
        if (sendEmotion)
            messageSender.send(senderId, "Your emotion is " + emotion);
        Track track = spotifyService.randomTrackForGenre(genre);
        return sendTrack(senderId, track, emotion, genre, Collections.emptySet());
    }

    private Response processGenre(String senderId, String emotion, String genre) throws IOException {
        log.info("Received genre: {}/{}", emotion, genre);
        Set<String> excludeGenres = new HashSet<>();
        if (genre.startsWith("-")) {
            excludeGenres.addAll(Arrays.asList(genre.substring(1).split(",")));
            genre = emotionAnalysisService.anyGenreInEmotionExcept(Emotion.of(emotion), excludeGenres);
            log.info("Derived genre: {}/{}", emotion, genre);
        }
        Track track = spotifyService.randomTrackForGenre(genre);
        return sendTrack(senderId, track, emotion, genre, excludeGenres);
    }

    private List<String> listenToReplies = Arrays.asList(
            "Please listen to",
            "This could be good for you",
            "I suggest you",
            "You might like",
            "Check this out",
            "How about",
            "Good song for you",
            "Hope you'll like this artist",
            "What about this album",
            "Hope it will fit your mood");

    private static Random random = new Random();

    private Response sendTrack(String senderId,
                               Track track,
                               String emotion,
                               String genre,
                               Set<String> excludeGenres) throws IOException {
        messageSender.sendImg(senderId, spotifyService.retrieveSpotifyImage(track));

        Set<String> newExcludeGenres = new HashSet<>(excludeGenres);
        newExcludeGenres.add(genre);

        ArrayList<String> buttons = new ArrayList<>(Arrays.asList("Open in Spotify",
                spotifyService.retrieveSpotifyUrl(track),
                COOL_I_WANT_MORE,
                emotion + "/" + genre));

        if (emotionAnalysisService.anyGenreInEmotionExcept(Emotion.of(emotion), newExcludeGenres) != null)
            buttons.addAll(Arrays.asList(ANOTHER_ONE_PLEASE, emotion + "/" + "-" + StringUtils.join(newExcludeGenres, ",")));

        return messageSender.sendBtns(senderId,
                listenToReplies.get(random.nextInt(listenToReplies.size())) + ": " + track.getArtists().get(0).getName() + " - " + track.getName(),
                buttons.toArray(new String[0]));
    }

    private Response sendFooterQuickReply(String senderId, int offset, int limit) throws IOException {
        QuickReplyBuilder builder = QuickReplyBuilder.builder();
        List<String> strings = Emotion.listEmotionsNoExclusiveFace();
        int len = strings.size();
        int to = offset + limit;
        boolean stillMoreAvail = to < len;
        List<String> strings1 = strings.subList(offset, Math.min(len, to));
        for (String emotion : strings1) {
            builder.addQuickReply(emotion);
        }
        if (stillMoreAvail)
            builder.addQuickReply("MORE", "+" + to);
        return messageSender.send(senderId, offset == 0 ? "Hey, how do you feel?" : "More emotions for you", builder.build());
    }

    private Response sendNoEmotion(String senderId) throws IOException {
        return messageSender.send(senderId, "No Emotion detected, please give it another try!");
    }

}
