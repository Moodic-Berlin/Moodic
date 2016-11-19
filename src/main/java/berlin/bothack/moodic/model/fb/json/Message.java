package berlin.bothack.moodic.model.fb.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author vgorin
 *         file created on 11/19/16 2:41 PM
 */


public class Message {
	public String mid;
	public String seq;
	public String text;
	@JsonProperty("quick_reply") public QuickReply quickReply;
	@JsonProperty("quick_replies")  public List<QuickReply> quickReplies;
	@JsonProperty("is_echo") public String isEcho;
	@JsonProperty("app_id") public String appId;
	public String metadata;
	public List<Attachment> attachments;

	public Message() {
	}

	public Message(String text) {
		this.text = text;
	}
}
