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
            String value = titlesUrls[i + 1];
            boolean isUrl = value.toLowerCase().startsWith("http:") || value.toLowerCase().startsWith("https:");
            Button button = new Button();
            button.type = isUrl ? "web_url" : "postback";
            button.title = titlesUrls[i];
            if (isUrl)
                button.url = value;
            else
                button.payload = value;
            payload.buttons.add(button);
        }
        return attachment;
    }
}
