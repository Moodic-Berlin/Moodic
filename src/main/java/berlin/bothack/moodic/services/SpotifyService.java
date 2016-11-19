package berlin.bothack.moodic.services;

import berlin.bothack.moodic.enums.Emotion;
import berlin.bothack.moodic.enums.Genre;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.ArtistSearchRequest;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.models.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static berlin.bothack.moodic.enums.Genre.*;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
@Service
public class SpotifyService {
    private static final String SPOTIFY_CLIENT_ID = "2e2a3b86cbe24519afac80669c071c0a";
    private static final String SPOTIFY_CLIENT_SECRET = "85ee4f6118b24c0ca3c3711d5f1d3c27";
    private static final String SPOTIFY_REDIRECT_URI = "https://moodic.xonix.info/spotifyWebhook";
    private static final String SPOTIFY_KEY = "spotify";
    private static final Random random;
    private static final ConcurrentMap<Emotion, Genre[]> emotionsMap = new ConcurrentHashMap<>();
    private final Logger log = LoggerFactory.getLogger(getClass());

    static {
        random = new Random();
/*
        emotionsMap.put(Emotion.ANGER, new Genre[]{ELECTRONICA, METAL, ROCK});
        emotionsMap.put(Emotion.CONTEMPT, new Genre[]{ELECTRONICA, METAL, ROCK});
        emotionsMap.put(Emotion.DISGUST, new Genre[]{ELECTRONICA, METAL, ROCK});
        emotionsMap.put(Emotion.FEAR, new Genre[]{CHIPTUNE, COUNTRY_ROCK, SMOOTH_JAZZ});
        emotionsMap.put(Emotion.HAPPINESS, new Genre[]{ELECTRONICA, POP, REGGAE, ROCK, SOUL});
        emotionsMap.put(Emotion.JOYFUL, new Genre[]{ELECTRONICA, FUNK, POP, REGGAE, ROCK});
        emotionsMap.put(Emotion.NEUTRAL, new Genre[]{BLUES, CLASSICAL, ELECTRONICA, FUNK, METAL, POP, REGGAE, ROCK, SOUL});
        emotionsMap.put(Emotion.SADNESS, new Genre[]{BLUES, CLASSICAL, ROCK, SOUL});
        emotionsMap.put(Emotion.SLEEPY, new Genre[]{BLUES, CLASSICAL, JAZZ});
        emotionsMap.put(Emotion.SURPRISE, new Genre[]{BLUEGRASS, DANCE_PUNK, FUNK, HAPPY_HARDCORE, JUNGLE});
*/
    }

    private Api setup() {
        return Api.builder()
                .clientId(SPOTIFY_CLIENT_ID)
                .clientSecret(SPOTIFY_CLIENT_SECRET)
                .redirectURI(SPOTIFY_REDIRECT_URI)
                .build();
    }

    public Set<String> retrieveSpotifyUrls(List<Track> tracks) {
        Set<String> urls = new LinkedHashSet<>();

        tracks.stream().filter(track -> track.getExternalUrls() != null && track.getExternalUrls().get(SPOTIFY_KEY) != null).collect(Collectors.toList()).forEach(track -> {
            urls.add(retrieveSpotifyUrl(track));
        });
        return urls;
    }

    public String retrieveSpotifyUrl(final Track track) {
        return track.getExternalUrls().get(SPOTIFY_KEY);
    }

    public Track randomTrackForGenre(String genre) {
        List<Track> tracks = searchTracksByGenre(genre);
        return tracks.get(random.nextInt(tracks.size()));
    }

    public List<Track> searchTracksByGenre(String genre) {
        final Api api = setup();

        try {
            final TrackSearchRequest trackSearchRequest = api.searchTracks(buildGenresQuery(genre)).build();
            return trackSearchRequest.get().getItems();
            //log.info(trackSearchRequest.getJson());
        } catch (Exception e) {
            log.debug("Something went wrong!" + e.getMessage());
        }
        return null;
    }

    public void searchArtistsByGenre(String genre) {
        final Api api = setup();

        try {
            final ArtistSearchRequest artistSearchRequest = api.searchArtists(buildGenresQuery(genre)).build();

            log.info(artistSearchRequest.getJson());
        } catch (Exception e) {
            log.debug("Something went wrong!" + e.getMessage());
        }
    }

    private String buildGenresQuery(String... genres) {
        String query = "genre:";
        for (String genre : genres) {
            query += "\"" + genre + "\"" + ",";
        }
        return query.substring(0, query.length() - 1);
    }
}
