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
import berlin.bothack.moodic.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
	private final LogicService logicService;

	@Autowired
	public FacebookController(Messages messages, SpotifyService spotifyService, Conf conf, MessageSender messageSender, MicrosoftCognitiveService microsoftCognitiveService, LogicService logicService) {
		this.spotifyService = spotifyService;
		this.conf = conf;
		this.messageSender = messageSender;
		this.messages = messages;
		this.microsoftCognitiveService = microsoftCognitiveService;
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
				String imageUrl = getImageUrl(messaging);
				String quickReply = getQuickReply(messaging);

				if(imageUrl != null) {
					processImage(senderId, imageUrl);
				}
				else if(quickReply != null) {
					processQuickReply(senderId, quickReply);
				}
				else {
					log.info("unknown action for {}", senderId);
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

	private Response processTextMessage(String senderId, String text) throws IOException {
		// TODO: your custom impl goes here
		Response response = messageSender.send(senderId, text);
		if(response != null) {
			log.info("message {} sent back to {} successfully!", text, senderId);
		}
		else {
			log.warn("error replying back to {}", senderId);
		}
		return response;
	}

	private Response processImage(String senderId, String imageUrl) throws IOException {
		try {
			EmotionResponse emotionResponse = microsoftCognitiveService.retrieveEmotion(imageUrl);
			log.info("Emotion Response is {}", emotionResponse);
			Emotion emotion = microsoftCognitiveService.getMostLikableEmotion(emotionResponse);
			String genre = logicService.emotionToGenre(emotion.name());
			log.info("Received following quick reply action: {}, corresponding genre is: {}", emotion.name(), genre);
			messageSender.send(senderId, "Your emotion is " + emotion.name());
			return messageSender.send(senderId, spotifyService.retrieveSpotifyUrl(spotifyService.randomTrackForGenre(genre)));
		} catch (Exception ex) {
			log.debug("Something went really wrong, imageUrl is : {}", imageUrl);
			return sendNoEmotion(senderId);
		}
	}

	private Response processQuickReply(String senderId, String payload) throws IOException {
		// TODO: your custom impl goes here
		Response response = messageSender.send(senderId, payload);
		if(response != null) {
			log.info("message {} sent back to {} successfully!", payload, senderId);
		}
		else {
			log.warn("error replying back to {}", senderId);
		}
		return response;
	}

	private Response sendFooterQuickReply(String senderId) throws IOException {
		QuickReplyBuilder builder = QuickReplyBuilder.builder();
		for (String emotion : spotifyService.listEmotions()) {
			builder.addQuickReply(emotion);
		}
		return messageSender.send(senderId, messages.get("dabot.howDoYouFeel"), builder.build());
	}

	private Response sendNoEmotion(String senderId) throws IOException {
		return messageSender.send(senderId, "No Emotion detected, please give it another try!");
	}

}
