package berlin.bothack.moodic.util;

import java.util.regex.Pattern;

/**
 * @author vgorin
 *         file created on 11/19/16 2:23 PM
 */


public class PropertyUtil {

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
