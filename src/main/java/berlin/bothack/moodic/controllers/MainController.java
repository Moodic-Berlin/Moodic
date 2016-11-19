package berlin.bothack.moodic.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/html")
    public String getHome() {
        return "Hi, I'm Moodic bot. " +
                "Find me at <a href=https://fb.me/Moodic/>fb.me/Moodic</a> or <a href=https://m.me/Moodic>m.me/Moodic</a>."; // TODO: fixme if changed
    }
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public String getStatus() {
        return "success";
    }
}
