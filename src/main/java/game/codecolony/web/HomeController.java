package game.codecolony.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(final Model model) {
        model.addAttribute("missionTitle", "Mission Briefing");
        model.addAttribute(
                "missionSummary",
                "Helix Dynamics has assigned you to oversee the recovery of a remote colony site from orbit around Eryndor-IV."
        );
        model.addAttribute(
                "missionObjective",
                "Investigate the colony site and restore critical systems in stages."
        );
        return "intro";
    }
}
