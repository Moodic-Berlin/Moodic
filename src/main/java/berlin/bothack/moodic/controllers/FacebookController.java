package berlin.bothack.moodic.controllers;

import berlin.bothack.moodic.model.fb.MessageSender;
import berlin.bothack.moodic.model.fb.json.Callback;
import berlin.bothack.moodic.model.fb.json.Entry;
import berlin.bothack.moodic.model.fb.json.Messaging;
import berlin.bothack.moodic.model.fb.json.Response;
import berlin.bothack.moodic.services.SpotifyService;
import berlin.bothack.moodic.util.Messages;
import berlin.bothack.moodic.util.PropertyUtil;
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
	private final Messages messages;
	private final SpotifyService spotifyService;

	@Autowired
	public FacebookController(Messages messages, SpotifyService spotifyService) {
		this.messages = messages;
		this.spotifyService = spotifyService;
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
		if(PropertyUtil.FB_VERIFY_TOKEN.equals(token)) {
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
					Response response = MessageSender.send(senderId, text);
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
