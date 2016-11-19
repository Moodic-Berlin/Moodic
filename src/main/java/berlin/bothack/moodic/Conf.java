package berlin.bothack.moodic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Conf {
    @Value("${FB_VERIFY_TOKEN}")
    public String FB_VERIFY_TOKEN;
    @Value("${FB_PAGE_ACCESS_TOKEN}")
    public String FB_PAGE_ACCESS_TOKEN;

    public String getFB_POST_URL() {
        return String.format("%s?access_token=%s", FB_END_POINT, FB_PAGE_ACCESS_TOKEN);
    }

    public static String FB_END_POINT = "https://graph.facebook.com/v2.6/me/messages";
}
