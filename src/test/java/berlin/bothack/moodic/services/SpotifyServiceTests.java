package berlin.bothack.moodic.services;

import org.junit.Test;

import java.util.Set;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
public class SpotifyServiceTests {
    @Test
    public void testSearchArtistByGenreSouthernHipHop() {
        SpotifyService spotifyMockService = new SpotifyService();
        spotifyMockService.searchArtistsByGenre("southern hip hop");
    }
}
