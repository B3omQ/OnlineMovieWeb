package fa.project.onlinemovieweb.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MediaVideoController {

    @GetMapping("/Media")
    public String Media(){
        return "mediaVideo";
    }
}
