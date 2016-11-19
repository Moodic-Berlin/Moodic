package berlin.bothack.moodic.controllers;

import berlin.bothack.moodic.Conf;
import berlin.bothack.moodic.model.fb.MessageSender;
import berlin.bothack.moodic.model.fb.QuickReplyBuilder;
import berlin.bothack.moodic.model.fb.json.*;
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

	@Autowired
	public FacebookController(Messages messages, SpotifyService spotifyService, Conf conf, MessageSender messageSender) {
		this.spotifyService = spotifyService;
		this.conf = conf;
		this.messageSender = messageSender;
		this.messages = messages;
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
				if(messaging.message != null) {
					String text = messaging.message.text;
					Response response = messageSender.send(senderId, text, QuickReply.of("Yes", "No"));
					if(response != null) {
						log.info("message {} sent back to {} successfully!", text, senderId);
					}
					else {
						log.warn("error replying back to {}", senderId);
					}
				}
			}
		}
		return "";
	}

	private void sendFooterQuickReply(String senderId) throws IOException {
		QuickReplyBuilder builder = QuickReplyBuilder.builder();
		for (String emotion : spotifyService.listEmotions()) {
			builder.addQuickReply(emotion);
		}
		messageSender.send(senderId, messages.get("dabot.howDoYouFeel"), builder.build());
	}

	private void sendNoEmotion(String senderId) throws IOException {
		messageSender.send(senderId, "No Emotion detected, please give it another try!");
	}

}
