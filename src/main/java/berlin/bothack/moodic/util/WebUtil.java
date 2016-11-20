package berlin.bothack.moodic.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public final class WebUtil {
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
    }

    public static String getRequestDomain(boolean forceHttps) {
        HttpServletRequest request = getRequest();
        if (request == null)
            return null;
        return (forceHttps ? "https" : request.getScheme()) + "://" +   // "http" + "://
                request.getServerName() +       // "myhost"
                (forceHttps ? "" : ":" + request.getServerPort());
    }

    private WebUtil() {
    }
}
