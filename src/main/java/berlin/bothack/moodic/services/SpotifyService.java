package berlin.bothack.moodic.services;

import berlin.bothack.moodic.model.eventful.Concert;
import berlin.bothack.moodic.model.eventful.EventfulDTO;
import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.ArtistSearchRequest;
import com.wrapper.spotify.methods.TopTracksRequest;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.Image;
import com.wrapper.spotify.models.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
@Service
public class SpotifyService {
    private static final String SPOTIFY_CLIENT_ID = "2e2a3b86cbe24519afac80669c071c0a";
    private static final String SPOTIFY_CLIENT_SECRET = "85ee4f6118b24c0ca3c3711d5f1d3c27";
    private static final String SPOTIFY_REDIRECT_URI = "https://moodic.xonix.info/spotifyWebhook";
    private static final String SPOTIFY_KEY = "spotify";
    private static final Random random = new Random();
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final EventfulService eventfulService;

    private Api api;

    @Autowired
    public SpotifyService(EventfulService eventfulService) {
        this.eventfulService = eventfulService;
        setup();
    }

    @Scheduled(fixedRate = 1_800_000)
    private void setup() {
        log.info("Resetting Spotify API to refresh access token...");
        api = Api.builder()
                .clientId(SPOTIFY_CLIENT_ID)
                .clientSecret(SPOTIFY_CLIENT_SECRET)
                .redirectURI(SPOTIFY_REDIRECT_URI)
                .build();
        ClientCredentialsGrantRequest request = api.clientCredentialsGrant().build();
        SettableFuture<ClientCredentials> responseFuture = request.getAsync();
        Futures.addCallback(responseFuture, new FutureCallback<ClientCredentials>() {
            @Override
            public void onSuccess(ClientCredentials clientCredentials) {
                log.info("Successfully retrieved an access token! " + clientCredentials.getAccessToken());
                log.info("The access token expires in " + clientCredentials.getExpiresIn() + " seconds");

                api.setAccessToken(clientCredentials.getAccessToken());
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.error("Error refresh spotify token", throwable);
            }
        });
    }

    public Set<String> retrieveSpotifyUrls(List<Track> tracks) {
        Set<String> urls = new LinkedHashSet<>();

        tracks.stream().filter(track -> track.getExternalUrls() != null && track.getExternalUrls().get(SPOTIFY_KEY) != null).collect(Collectors.toList()).forEach(track -> {
            urls.add(retrieveSpotifyUrl(track));
        });
        return urls;
    }

    public String retrieveSpotifyUrl(Track track) {
        return track.getExternalUrls().get(SPOTIFY_KEY);
    }

    public String retrieveSpotifyImage(Track track) {
        List<Image> images = track.getAlbum().getImages();
        if (images.isEmpty())
            return null;
        return images.get(0).getUrl();
    }

    public Track randomTrackForGenre(String genre) {
        List<Track> tracks = searchTracksByGenre(genre);
        return tracks.get(random.nextInt(tracks.size()));
    }

    public EventfulDTO commercialTrackForGenre(String genre) throws EVDBRuntimeException, EVDBAPIException {
        EventfulDTO eventfulDTO = new EventfulDTO();
        Concert concert = eventfulService.searchConcert(genre);
        eventfulDTO.setConcert(concert);
        Artist artist = searchArtist(concert.getTitle());
        eventfulDTO.setTrack(findTopTrack(artist.getId()));
        return eventfulDTO;
    }

    public List<Track> searchTracksByGenre(String genre) {
        try {
            final TrackSearchRequest trackSearchRequest = api.searchTracks(buildGenresQuery(genre)).build();
            return trackSearchRequest.get().getItems();
            //log.info(trackSearchRequest.getJson());
        } catch (Exception e) {
            log.error("Something went wrong!" + e.getMessage());
        }
        return null;
    }

    public void searchArtistsByGenre(String genre) {
        try {
            final ArtistSearchRequest artistSearchRequest = api.searchArtists(buildGenresQuery(genre)).build();

            log.info(artistSearchRequest.getJson());
        } catch (Exception e) {
            log.debug("Something went wrong!" + e.getMessage());
        }
    }

    public Artist searchArtist(String name) {
        try {
            final ArtistSearchRequest artistSearchRequest = api.searchArtists(name).build();

            return artistSearchRequest.get().getItems().get(0);
        } catch (Exception e) {
            log.debug("Something went wrong!" + e.getMessage());
        }
        return null;
    }

    public Track findTopTrack(String artistId) {
        final String country = "US";
        try {
            final TopTracksRequest topTracksRequest = api.getTopTracksForArtist(artistId, country).build();
            return topTracksRequest.get().get(0);
        } catch (Exception e) {
            log.debug("Something went wrong!" + e.getMessage());
        }
        return null;
    }


    private String buildGenresQuery(String... genres) {
        String query = "genre:";
        for (String genre : genres) {
            query += "\"" + genre + "\"" + ",";
        }
        return query.substring(0, query.length() - 1);
    }
}
