package berlin.bothack.moodic.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

/**
 * @author vgorin
 *         file created on 11/19/16 1:36 PM
 */


@RestController
public class FacebookController {
	private final Logger log = LoggerFactory.getLogger(getClass());

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
		if("V0qG96lHz1u8f5uOsRYA".equals(token)) {
			log.info("webhook verify ok, token = {}", token);
			return challenge;
		}
		log.warn("webhook verify failed, token = {}", token);
		return String.format("wrong token! challenge = %s, token = %s", challenge, token);
	}

}