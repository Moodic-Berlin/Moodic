package berlin.bothack.moodic.model.fb.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author vgorin
 *         file created on 11/19/16 4:26 PM
 */


public class Response {
	@JsonProperty("recipient_id") public String recipientId;
	@JsonProperty("message_id") public String messageId;
}
