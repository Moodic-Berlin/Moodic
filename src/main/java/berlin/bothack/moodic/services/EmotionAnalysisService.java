package berlin.bothack.moodic.services;

import berlin.bothack.moodic.enums.Emotion;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class EmotionAnalysisService {
    private static class EmotionGenreRow {
        final Emotion emotion;
        final String genre;
        final double low;
        final double high;

        public EmotionGenreRow(String row) {
            String[] parts = row.split(",");
            genre = parts[0];
            emotion = Emotion.of(parts[1]);
            low = Double.parseDouble(parts[2]);
            high = Double.parseDouble(parts[3]);
        }
    }

    private static Set<String> genres = new HashSet<>();

    private static List<EmotionGenreRow> emotionGenreRows = parseEmotion2Genre();

    private static List<EmotionGenreRow> parseEmotion2Genre() {
        String content = null;
        try {
            content = IOUtils.toString(EmotionAnalysisService.class.getResourceAsStream("/emotion2genre.csv"), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] parts = content.split("\n");
        List<EmotionGenreRow> res = new ArrayList<>(parts.length);
        for (String part : parts) {
            EmotionGenreRow emotionGenreRow = new EmotionGenreRow(part);
            res.add(emotionGenreRow);
            genres.add(emotionGenreRow.genre);
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

    public String emotionToGenre(String emotion) {
        return emotionToGenre(Emotion.of(emotion));
    }

    public String anyGenreInEmotionExcept(Emotion emotion, Set<String> excludeGenres) {
        Set<String> genresOfEmotion = new HashSet<>();
        for (EmotionGenreRow row : emotionGenreRows) {
            if (row.emotion == emotion)
                genresOfEmotion.add(row.genre);
        }
        genresOfEmotion.removeAll(excludeGenres);
        if (genresOfEmotion.isEmpty())
            return null;
        List<String> l = new ArrayList<>(genresOfEmotion);
        Collections.shuffle(l);
        return l.get(0);
    }
}
