package berlin.bothack.moodic.model.eventful;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
public class Concert {
    private String title;
    private String url;

    @Override
    public String toString() {
        return "Concert{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", spotifyGenre='" + spotifyGenre + '\'' +
                '}';
    }

    private String spotifyGenre;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSpotifyGenre() {
        return spotifyGenre;
    }

    public void setSpotifyGenre(String spotifyGenre) {
        this.spotifyGenre = spotifyGenre;
    }
}
