package berlin.bothack.moodic.controllers;

import berlin.bothack.moodic.Conf;
import berlin.bothack.moodic.enums.Emotion;
import berlin.bothack.moodic.model.fb.MessageSender;
import berlin.bothack.moodic.model.fb.QuickReplyBuilder;
import berlin.bothack.moodic.model.fb.json.*;
import berlin.bothack.moodic.model.microsoft.cognitive.EmotionResponse;
import berlin.bothack.moodic.services.LogicService;
import berlin.bothack.moodic.services.MicrosoftCognitiveService;
import berlin.bothack.moodic.services.SpotifyService;
import berlin.bothack.moodic.services.WatsonConversationService;
import berlin.bothack.moodic.util.Messages;
import com.wrapper.spotify.models.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author vgorin
 *         file created on 11/19/16 1:36 PM
 */


@RestController
public class FacebookController {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final SpotifyService spotifyService;
	private final Conf conf;
	private final MessageSender messageSender;
	private final Messages messages;
	private final MicrosoftCognitiveService microsoftCognitiveService;
	private final WatsonConversationService watsonConversationService;
	private final LogicService logicService;

	@Autowired
	public FacebookController(
			Messages messages,
			SpotifyService spotifyService,
			Conf conf,
			MessageSender messageSender,
			MicrosoftCognitiveService microsoftCognitiveService,
			WatsonConversationService watsonConversationService,
			LogicService logicService
	) {
		this.spotifyService = spotifyService;
		this.conf = conf;
		this.messageSender = messageSender;
		this.messages = messages;
		this.microsoftCognitiveService = microsoftCognitiveService;
		this.watsonConversationService = watsonConversationService;
		this.logicService = logicService;
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
		if(conf.FB_VERIFY_TOKEN.equals(token)) {
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
		for(Entry entry: callback.entry) {
			for(Messaging messaging: entry.messaging) {
				String senderId = messaging.sender.id;
				try {
					String imageUrl = getImageUrl(messaging);
					String quickReply = getQuickReply(messaging);
					String text = getTextMessage(messaging);

					int offset = 0;
					if (quickReply != null && quickReply.startsWith("+"))
						offset = Integer.parseInt(quickReply);
					else {
						if (imageUrl != null) {
							processImage(senderId, imageUrl);
						} else if (quickReply != null) {
							processQuickReply(senderId, quickReply);
						} else if (text != null) {
							processTextMessage(senderId, text);
						} else {
							log.info("unknown action for {}", senderId);
						}
					}
					sendFooterQuickReply(senderId, offset, 7);
				} catch (Exception ex) {
					messageSender.send(senderId, "Ups, we've got an error \uD83D\uDE1E: " + ex);
				}
			}
		}
		return "";
	}


	private String getImageUrl(Messaging messaging) {
		if(messaging.message != null && messaging.message.attachments != null && messaging.message.attachments.size() > 0) {
			Payload payload = messaging.message.attachments.iterator().next().payload;
			if(payload != null && payload.url != null) {
				return payload.url;
			}
		}
		return null;
	}

	private String getQuickReply(Messaging messaging) {
		if(messaging.message != null && messaging.message.quickReply != null && messaging.message.quickReply.payload != null) {
			return messaging.message.quickReply.payload;
		}
		return null;
	}

	private String getTextMessage(Messaging messaging) {
		if(messaging.message != null && messaging.message.text != null) {
			return messaging.message.text;
		}
		return null;
	}

	private Response processTextMessage(String senderId, String text) throws IOException {
		try {
			Emotion emotion = watsonConversationService.retrieveEmotion(text);
			log.info("text {} converted emotion {}", text, emotion);
			return processEmotion(senderId, emotion.name());
		}
		catch (Exception ex) {
			log.warn("error processing text to emotion/genre: {}", text);
			return sendNoEmotion(senderId);
		}
	}

	private Response processImage(String senderId, String imageUrl) throws IOException {
		try {
			EmotionResponse emotionResponse = microsoftCognitiveService.retrieveEmotion(imageUrl);
			log.info("Emotion Response is {}", emotionResponse);
			Emotion emotion = microsoftCognitiveService.getMostLikableEmotion(emotionResponse);
			return processEmotion(senderId, emotion.name());
		}
		catch (Exception ex) {
			log.warn("error processing image to emotion/genre: {}", imageUrl);
			return sendNoEmotion(senderId);
		}
	}

	private Response processQuickReply(String senderId, String payload) throws IOException {
		String genre = logicService.emotionToGenre(payload);
		log.info("Received following quick reply action: {}, corresponding genre is: {}", payload, genre);
		Track track = spotifyService.randomTrackForGenre(genre);
		return sendTrack(senderId, track);
	}

	private Response processEmotion(String senderId, String emotion) throws IOException {
		String genre = logicService.emotionToGenre(emotion);
		log.info("Received following quick reply action: {}, corresponding genre is: {}", emotion, genre);
		messageSender.send(senderId, "Your emotion is " + emotion);
		Track track = spotifyService.randomTrackForGenre(genre);
		return sendTrack(senderId, track);
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
	private Response sendTrack(String senderId, Track track) throws IOException {
		messageSender.sendImg(senderId, spotifyService.retrieveSpotifyImage(track));
		return messageSender.sendWebBtns(senderId,
				listenToReplies.get(random.nextInt(listenToReplies.size())) + ": " + track.getArtists().get(0).getName() + " - " + track.getName(),
				"Open in Spotify",
				spotifyService.retrieveSpotifyUrl(track));
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
