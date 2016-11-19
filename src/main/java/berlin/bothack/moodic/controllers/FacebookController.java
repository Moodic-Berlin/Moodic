package berlin.bothack.moodic.controllers;

import berlin.bothack.moodic.Conf;
import berlin.bothack.moodic.model.fb.MessageSender;
import berlin.bothack.moodic.model.fb.json.*;
import berlin.bothack.moodic.services.SpotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

	@Autowired
	public FacebookController(SpotifyService spotifyService, Conf conf, MessageSender messageSender) {
		this.spotifyService = spotifyService;
		this.conf = conf;
		this.messageSender = messageSender;
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

}
