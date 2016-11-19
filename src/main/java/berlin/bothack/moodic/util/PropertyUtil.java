package berlin.bothack.moodic.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vgorin
 *         file created on 11/19/16 2:23 PM
 */


public class PropertyUtil {
	public static final String FB_VERIFY_TOKEN = PropertyUtil.loadProperty("VERIFY_TOKEN", "V0qG96lHz1u8f5uOsRYA");
	public static final String FB_END_POINT = "https://graph.facebook.com/v2.6/me/messages";
	public static final String FB_PAGE_ACCESS_TOKEN = PropertyUtil.loadProperty("ACCESS_TOKEN", "EAAZA0fBd0Nd4BAIGCIWq9JSGwZBYgPuT4hcrPJVmPrnbg5kYkUZAftWKQdJf0ty98qrW2vlNXSHKXWZC0gZAa24EdF8ZBvz4NOIGMfalXtAZBmtDXiFG3gnkBvErCbHbioATllSNBfxBgylWgB1fJ2WbvH2rC8kWbEzwKHLWollxQZDZD");
	public static final String FB_POST_URL = String.format("%s?access_token=%s", FB_END_POINT, FB_PAGE_ACCESS_TOKEN);
	private static final Logger log = LoggerFactory.getLogger(PropertyUtil.class);

	static {
		log.info("FB_VERIFY_TOKEN (VERIFY_TOKEN): {}", FB_VERIFY_TOKEN);
		log.info("FB_END_POINT: {}", FB_END_POINT);
		log.info("FB_PAGE_ACCESS_TOKEN (ACCESS_TOKEN): {}", FB_PAGE_ACCESS_TOKEN);
		log.info("FB_POST_URL: {}", FB_POST_URL);
	}

	public static String loadProperty(String key, String defaultValue) {
		// try to load property as is
		String value = loadProperty(key);

		// try to load it lowercase, convert SOME_KEY to some-key
		if(value == null) {
			key = key.toLowerCase().replaceAll("_", "-");
			value = loadProperty(key);
		}

		// try to load it uppercase, convert some-key to SOME_KEY
		if(value == null) {
			key = key.toUpperCase().replaceAll("\\-", "_");
			value = loadProperty(key);
		}

		// fallback to default value
		return value == null? defaultValue: value;
	}

	private static String loadProperty(String key) {
		// try to load property from JAVA_OPTS like -Dname=value
		String value = System.getProperty(key);

		// fallback to system environment variable if value is null
		return value == null? System.getenv(key): value;
	}
}
