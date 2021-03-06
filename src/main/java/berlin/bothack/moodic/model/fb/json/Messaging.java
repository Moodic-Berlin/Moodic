package berlin.bothack.moodic.model.fb.json;

/**
 * @author vgorin
 *         file created on 11/19/16 2:53 PM
 */


public class Messaging {
	public Sender sender;
	public Recipient recipient;
	public String timestamp;
	public Message message;
	public Delivery delivery;
	public Postback postback;
	public Read read;

	public Messaging() {
	}

	public Messaging(Recipient recipient, Message message) {
		this.recipient = recipient;
		this.message = message;
	}
}
