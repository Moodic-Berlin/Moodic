package berlin.bothack.moodic.services;

import com.evdb.javaapi.APIConfiguration;
import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;
import com.evdb.javaapi.data.Event;
import com.evdb.javaapi.data.SearchResult;
import com.evdb.javaapi.data.request.EventSearchRequest;
import com.evdb.javaapi.operations.EventOperations;
import org.junit.Test;

import java.util.List;

public class EvdbTests {
    @Test
    public void test1() throws EVDBRuntimeException, EVDBAPIException {
//        APIConfiguration apiConfiguration = new APIConfiguration();
        APIConfiguration.setApiKey("pMcpkZD9DfnFF8D7");
        APIConfiguration.setEvdbUser("vladg");
        APIConfiguration.setEvdbPassword("16fZThvWN3");
        EventOperations eo = new EventOperations();
        EventSearchRequest esr = new EventSearchRequest();

        esr.setLocation("Berlin");
//        esr.setDateRange("2012050200-2013052100");
        esr.setDateRange("2016050200-2016311200");
//        esr.setPageSize(20);
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
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            System.out.println(i + " " + event.getTitle() + ", " + event.getPerformers());
        }
    }
}
