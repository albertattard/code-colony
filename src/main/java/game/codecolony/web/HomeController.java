package game.codecolony.web;

import game.codecolony.content.NarrativeContentService;
import game.codecolony.content.NarrativeContentService.IntroNarrativeContent;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public final class HomeController {

    private final NarrativeContentService narrativeContentService;

    public HomeController(final NarrativeContentService narrativeContentService) {
        this.narrativeContentService = narrativeContentService;
    }

    @GetMapping("/")
    public String home(final Model model) {
        final IntroNarrativeContent introNarrative = narrativeContentService.loadIntroNarrative();
        model.addAttribute("missionTitle", introNarrative.title());
        model.addAttribute("missionSummary", introNarrative.summary());
        model.addAttribute("missionObjective", introNarrative.objective());
        model.addAttribute("briefingTitle", introNarrative.briefingTitle());
        model.addAttribute("briefingHtml", introNarrative.briefingHtml());
        return "intro";
    }
}
