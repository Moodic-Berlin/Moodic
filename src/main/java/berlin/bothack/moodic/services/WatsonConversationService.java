package berlin.bothack.moodic.services;

import berlin.bothack.moodic.enums.Emotion;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
@Service
public class WatsonConversationService {
    private final static String PASSWORD = "nWTuKmM34l73";
    private final static String USERNAME = "f563956b-0761-4533-99d1-088746755517";
    private final static String WORKSPACE_ID = "952bda08-0c72-49ca-ab65-eafbd42fe6c6";
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Emotion retrieveEmotion(String text) {
        ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2016_07_11);
        service.setUsernameAndPassword(USERNAME, PASSWORD);

        MessageRequest newMessage = new MessageRequest.Builder().inputText(text).build();
        MessageResponse response = service.message(WORKSPACE_ID, newMessage).execute();
        Emotion emotion = Emotion.NEUTRAL;
        if (response.getEntities() != null && response.getEntities().size() > 0) {
            try {
                emotion = Emotion.of(response.getEntities().get(0).getValue());
            } catch (Exception ex) {
                log.warn("Weren't able to parse emotion, using neutral", ex);
            }
        } else {
            log.warn("Hmmm, seems we can't determine emotion for: " + text);
        }

        return emotion;
    }
}
