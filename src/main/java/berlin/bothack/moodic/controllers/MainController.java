package berlin.bothack.moodic.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/html")
    public String getHome() {
        return "<h2>Hi, I'm Moodic bot. " +
                "Find me at <a href=https://fb.com/dabot69>fb.com/dabot69</a> or <a href=https://m.me/dabot69>m.me/dabot69</a>." +
                "</h2>"; // TODO: fixme if changed
    }
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public String getStatus() {
        return "success";
    }
}
