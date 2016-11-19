package berlin.bothack.moodic.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public String getStatus() {
        return "success";
    }
}
