package berlin.bothack.moodic.model.fb;

import berlin.bothack.moodic.model.fb.json.Message;
import berlin.bothack.moodic.model.fb.json.Recipient;
import berlin.bothack.moodic.model.fb.json.Response;
import berlin.bothack.moodic.util.PropertyUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author vgorin
 *         file created on 11/19/16 3:48 PM
 */


public class MessageSender {
	private static final String FB_END_POINT = "https://graph.facebook.com/v2.6/me/messages";
	private static final String FB_PAGE_ACCESS_TOKEN = PropertyUtil.loadProperty("ACCESS_TOKEN", "EAAZA0fBd0Nd4BAIGCIWq9JSGwZBYgPuT4hcrPJVmPrnbg5kYkUZAftWKQdJf0ty98qrW2vlNXSHKXWZC0gZAa24EdF8ZBvz4NOIGMfalXtAZBmtDXiFG3gnkBvErCbHbioATllSNBfxBgylWgB1fJ2WbvH2rC8kWbEzwKHLWollxQZDZD");
	private static final String FB_POST_URL = String.format("%s?access_token=%s", FB_END_POINT, FB_PAGE_ACCESS_TOKEN);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();
	private static final Logger log = LoggerFactory.getLogger(MessageSender.class);

	static {
		OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
	}

	public static Response send(String recipientId, String text) throws IOException {
		return send(recipientId, new Message(text));
	}

	public static Response send(String recipientId, Message message) throws IOException {
		log.info("sending a message to {}, message = {}", recipientId, message);
		String jsonPayload = OBJECT_MAPPER.writeValueAsString(new Wrapper(recipientId, message));
		log.info("json payload for {} is {}", recipientId, jsonPayload);
		return sendJson(jsonPayload);
	}

	private static Response sendJson(String jsonPayload) throws IOException {
		HttpPost post = new HttpPost(FB_POST_URL);
		post.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

		log.info("HTTP POST {}\n{}", FB_POST_URL, jsonPayload);
		HttpResponse httpResponse = HTTP_CLIENT.execute(post);
		HttpEntity entity = httpResponse.getEntity();
		String response = EntityUtils.toString(entity);
		EntityUtils.consume(entity);
		StatusLine line = httpResponse.getStatusLine();
		if(line.getStatusCode() == 200) {
			log.info("200 OK");
			return OBJECT_MAPPER.readValue(response, Response.class);
		}
		else {
			log.warn("{} {}\n{}", line.getStatusCode(), line.getReasonPhrase(), response);
			return null;
		}
	}

	public static class Wrapper {
		public Recipient recipient;
		public Message message;

		Wrapper(String recipientId, Message message) {
			this.recipient = new Recipient(recipientId);
			this.message = message;
		}
	}
}
