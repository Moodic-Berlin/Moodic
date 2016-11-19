package berlin.bothack.moodic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Conf {
    @Value("${FB_VERIFY_TOKEN}")
    public String FB_VERIFY_TOKEN;// = PropertyUtil.loadProperty("VERIFY_TOKEN", "V0qG96lHz1u8f5uOsRYA");
    @Value("${FB_PAGE_ACCESS_TOKEN}")
    public String FB_PAGE_ACCESS_TOKEN; //= PropertyUtil.loadProperty("ACCESS_TOKEN", "EAAZA0fBd0Nd4BAIGCIWq9JSGwZBYgPuT4hcrPJVmPrnbg5kYkUZAftWKQdJf0ty98qrW2vlNXSHKXWZC0gZAa24EdF8ZBvz4NOIGMfalXtAZBmtDXiFG3gnkBvErCbHbioATllSNBfxBgylWgB1fJ2WbvH2rC8kWbEzwKHLWollxQZDZD");

    public String getFB_POST_URL() {
        return String.format("%s?access_token=%s", FB_END_POINT, FB_PAGE_ACCESS_TOKEN);
    }

    public static String FB_END_POINT = "https://graph.facebook.com/v2.6/me/messages";
}
