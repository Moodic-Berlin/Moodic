package berlin.bothack.moodic.model.eventful;

import com.wrapper.spotify.models.Track;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
public class EventfulDTO {
    private Concert concert;

    public Concert getConcert() {
        return concert;
    }

    public void setConcert(Concert concert) {
        this.concert = concert;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    private Track track;
}
