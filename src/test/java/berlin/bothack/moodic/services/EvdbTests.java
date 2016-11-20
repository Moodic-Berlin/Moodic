package berlin.bothack.moodic.services;

import berlin.bothack.moodic.model.eventful.Concert;
import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.Track;
import org.junit.Test;

public class EvdbTests {
    @Test
    public void test1() throws EVDBRuntimeException, EVDBAPIException {
        EventfulService eventfulService = new EventfulService();
        SpotifyService spotifyService = new SpotifyService();
        spotifyService.searchArtistsByGenre("southern hip hop");
        Concert concert = eventfulService.searchConcert("country");

        System.out.println(concert);

        Artist artist = spotifyService.searchArtist(concert.getTitle());
        Track track = spotifyService.findTopTrack(artist.getId());
        System.out.println(track.getName());

    }
}
