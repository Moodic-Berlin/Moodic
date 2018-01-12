package berlin.bothack.moodic.services;

import berlin.bothack.moodic.enums.Emotion;
import berlin.bothack.moodic.model.microsoft.cognitive.EmotionResponse;
import berlin.bothack.moodic.model.microsoft.cognitive.Scores;
import berlin.bothack.moodic.util.MapUtil;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static berlin.bothack.moodic.enums.Emotion.*;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
@Service
public class MicrosoftCognitiveService {
    private transient final Gson GSON;

    {
        GSON = new Gson();
    }

    private static final String key = "4ffe9bb603bb4ff3ac65c2405992e6ec";
    private final Logger log = LoggerFactory.getLogger(getClass());

    public String buildRequestJson(String url) {
        return "{ \"url\": \"" + url + "\" }";
    }

    public EmotionResponse retrieveEmotion(String url) {
        HttpClient httpclient = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize");


            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", key);


            // Request body
            StringEntity reqEntity = new StringEntity(buildRequestJson(url));
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String jsonString = EntityUtils.toString(entity).replace("[", "").replace("]", "");
                log.info("Microsoft Cognitive Services Emotion analyzed: {}", jsonString);
                return GSON.fromJson(jsonString, EmotionResponse.class);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Emotion getMostLikableEmotion(EmotionResponse emotionResponse) {
        Map<Emotion, Double> emotionDoubleMap = new HashMap<>();
        Scores scores = emotionResponse.getScores();
        emotionDoubleMap.put(ANGER, scores.getAnger());
        emotionDoubleMap.put(CONTEMPT, scores.getContempt());
        emotionDoubleMap.put(DISGUST, scores.getDisgust());
        emotionDoubleMap.put(FEAR, scores.getFear());
        emotionDoubleMap.put(HAPPINESS, scores.getHappiness());
        emotionDoubleMap.put(NEUTRAL, scores.getNeutral());
        emotionDoubleMap.put(SADNESS, scores.getSadness());
        emotionDoubleMap.put(SURPRISE, scores.getSurprise());
        return MapUtil.sortByValueDesc(emotionDoubleMap).entrySet().iterator().next().getKey();
    }
}
