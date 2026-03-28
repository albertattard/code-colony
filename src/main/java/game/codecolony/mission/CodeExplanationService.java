package game.codecolony.mission;

import game.codecolony.content.NarrativeContentService;
import game.codecolony.content.NarrativeContentService.MissionExplanationContent;

import org.springframework.stereotype.Service;

@Service
public final class CodeExplanationService {

    private final NarrativeContentService narrativeContentService;

    public CodeExplanationService(final NarrativeContentService narrativeContentService) {
        this.narrativeContentService = narrativeContentService;
    }

    public CodeExplanation explain(final String missionId) {
        final MissionExplanationContent explanation = narrativeContentService.loadMissionExplanation(missionId);
        return new CodeExplanation(explanation.headline(), explanation.explanationHtml());
    }
}
