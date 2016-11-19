package berlin.bothack.moodic.services;

import berlin.bothack.moodic.enums.Emotion;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class LogicService {
    private static class EmotionGenreRow {
        final Emotion emotion;
        final String genre;
        final double low;
        final double high;

        public EmotionGenreRow(String row) {
            String[] parts = row.split(",");
            genre = parts[0];
            emotion = Emotion.valueOf(parts[1].toUpperCase());
            low = Double.parseDouble(parts[2]);
            high = Double.parseDouble(parts[3]);
        }
    }

    private static List<EmotionGenreRow> emotionGenreRows = parseEmotion2Genre();

    private static List<EmotionGenreRow> parseEmotion2Genre() {
        String content = null;
        try {
            content = IOUtils.toString(LogicService.class.getResourceAsStream("/emotion2genre.csv"), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] parts = content.split("\n");
        List<EmotionGenreRow> res = new ArrayList<>(parts.length);
        for (String part : parts) {
            res.add(new EmotionGenreRow(part));
        }
        return res;
    }

    private Random random = new Random();

    String emotionToGenre(Emotion emotion) {
        double d = random.nextDouble();
        for (EmotionGenreRow row : emotionGenreRows) {
            if (row.emotion == emotion && row.low <= d && row.high > d)
                return row.genre;
        }
        return "wtf"; // XXX ???
    }
}
