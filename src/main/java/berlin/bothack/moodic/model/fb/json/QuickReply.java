package berlin.bothack.moodic.model.fb.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author vgorin
 *         file created on 11/19/16 2:41 PM
 */


public class QuickReply {
	@JsonProperty("content_type") public String contentType;
	public String title;
	public String payload;
	@JsonProperty("image_url") public String imageUrl;

	public QuickReply() {
	}

	public QuickReply(String title) {
		this(title, title);
	}

	public QuickReply(String title, String payload) {
		this.contentType = "text";
		this.title = title;
		this.payload = payload;
	}
}
