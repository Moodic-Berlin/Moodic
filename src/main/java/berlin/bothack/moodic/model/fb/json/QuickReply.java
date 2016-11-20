package berlin.bothack.moodic.model.fb.json;

import berlin.bothack.moodic.util.WebUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
		String imgTitle = StringUtils.capitalize(title.toLowerCase()) + ".png";
		InputStream imgStream = QuickReply.class.getResourceAsStream("/static/" + imgTitle);
		String domain = WebUtil.getRequestDomain(true);
		if (imgStream != null && domain != null) {
			imageUrl = domain + "/" + imgTitle;
		}
	}

	public static List<QuickReply> of(String... titles) {
		List<QuickReply> replies = new ArrayList<>(titles.length);
		for(String title: titles) {
			replies.add(new QuickReply(title));
		}
		return replies;
	}
}
