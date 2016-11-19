package berlin.bothack.moodic.model.fb.json;

import java.util.ArrayList;

/**
 * @author vgorin
 *         file created on 11/19/16 5:50 PM
 */


public class Attachment {
	public String type;
	public Payload payload;

	public static Attachment image(String imgUrl) {
		Attachment attachment = new Attachment();
		attachment.type = "image";
		attachment.payload = new Payload();
		attachment.payload.url = imgUrl;
		return attachment;
	}

	public static Attachment buttons(String text, String... titlesUrls) {
		Attachment attachment = new Attachment();
		attachment.type = "template";
		Payload payload = new Payload();
		attachment.payload = payload;
		payload.text = text;
		payload.templateType = "button";
		payload.buttons = new ArrayList<>();
		for (int i = 0; i < titlesUrls.length; i += 2) {
			Button button = new Button();
			button.type = "web_url";
			button.title = titlesUrls[i];
			button.url = titlesUrls[i + 1];
			payload.buttons.add(button);
		}
		return attachment;
	}
}
