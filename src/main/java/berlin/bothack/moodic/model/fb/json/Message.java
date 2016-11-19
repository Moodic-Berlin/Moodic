package berlin.bothack.moodic.model.fb.json;

import com.google.gson.annotations.SerializedName;

/**
 * @author vgorin
 *         file created on 11/19/16 2:41 PM
 */


public class Message {
	public String mid;
	public int seq;
	public String text;
	@SerializedName("quick_reply") public QuickReply quickReply;
	@SerializedName("is_echo") public boolean isEcho;
	@SerializedName("app_id") public long appId;
	public String metadata;
}
