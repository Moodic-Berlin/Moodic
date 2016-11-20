package berlin.bothack.moodic.services;

import berlin.bothack.moodic.model.eventful.Concert;
import com.evdb.javaapi.APIConfiguration;
import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;
import com.evdb.javaapi.data.Event;
import com.evdb.javaapi.data.SearchResult;
import com.evdb.javaapi.data.request.EventSearchRequest;
import com.evdb.javaapi.operations.EventOperations;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Oleksandr Shchetynin on 11/19/2016.
 */
@Component
public class EventfulService {

    private static final String apiKey = "pMcpkZD9DfnFF8D7";
    private static final String user = "vladg";
    private static final String pwd = "16fZThvWN3";
    static {
        APIConfiguration.setApiKey(apiKey);
        APIConfiguration.setEvdbUser(user);
        APIConfiguration.setEvdbPassword(pwd);
    }

    public Concert searchConcert(String spotifyGenre) throws EVDBRuntimeException, EVDBAPIException {
        Concert concert = new Concert();
        concert.setSpotifyGenre(spotifyGenre);
        String genre = convertToEventfulGenre(spotifyGenre);

        EventOperations eo = new EventOperations();
        EventSearchRequest esr = new EventSearchRequest();

        // TODO Refactor this hardcoded shit!
        esr.setLocation("Berlin");
        esr.setCategory(genre);
        esr.setDateRange("2016112000-2016123100");
        esr.setPageNumber(1);
        // These 2 lines will set the timeout to 60 seconds.Normally not needed
        // Unless you are using Google App Engine
        esr.setConnectionTimeout(60000);  // Used with Google App Engine only
        esr.setReadTimeout(60000);        // Used with Google App Engine only
        SearchResult sr = eo.search(esr);
        if (sr.getTotalItems() > 1) {
            System.out.println("Total items: " + sr.getTotalItems());
        }

        List<Event> events = sr.getEvents();
        if (events == null || events.size() == 0) {
            return null;
        }
        concert.setTitle(events.get(0).getTitle());
        concert.setUrl(events.get(0).getURL());
        return concert;
    }

    private String convertToEventfulGenre(String before) {
        // TODO implement conversion based on CSV
        return "music_" + before.toLowerCase();
    }
}
