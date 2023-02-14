package main.modules.testModule;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@SuppressWarnings("unused")
public class testController {

    @RequestMapping("/")
    public String string() {
        return "test";
    }

    @RequestMapping("/test")
    public String test() {
        return "Twitch4JHandler";
    }
}
