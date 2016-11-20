package berlin.bothack.moodic.services;

import org.junit.Test;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
public class SpotifyServiceTests {
    @Test
    public void testSearchArtistByGenreSouthernHipHop() {
        SpotifyService spotifyMockService = new SpotifyService(new EventfulService());
        spotifyMockService.searchArtistsByGenre("southern hip hop");
    }
}
