package berlin.bothack.moodic.model.fb.json;

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
}
