package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MissionBehaviorLoaderTest {

    private final MissionBehaviorLoader missionBehaviorLoader = new MissionBehaviorLoader();

    @Test
    void allCurrentMissionBehaviorsLoad() {
        assertThat(missionBehaviorLoader.load("mission-01")).isNotNull();
        assertThat(missionBehaviorLoader.load("mission-02")).isNotNull();
        assertThat(missionBehaviorLoader.load("mission-03")).isNotNull();
    }

    @Test
    void loadMissionOneBehaviorFromResources() {
        final MissionBehaviorConfig behavior = missionBehaviorLoader.load("mission-01");

        assertThat(behavior.version()).isEqualTo(1);
        assertThat(behavior.missionId()).isEqualTo("mission-01");
        assertThat(behavior.allowedCommands()).containsExactly("Core.connect()");
        assertThat(behavior.allowedRuntimeCommands()).containsExactly("connect");
        assertThat(behavior.execution().temporaryDirectoryPrefix()).isEqualTo("wake-the-core-");
        assertThat(behavior.execution().resultFileName()).isEqualTo("wake-the-core-result.properties");
        assertThat(behavior.execution().compilationFailureSummary())
                .isEqualTo("The code could not be compiled for Mission 01.");
        assertThat(behavior.execution().executionStoppedSummary())
                .isEqualTo("Execution stopped before Mission 01 could be evaluated.");
        assertThat(behavior.execution().initialStatusNoteTemplate())
                .isEqualTo("No telemetry available while offline.");
        assertThat(behavior.objective().kind()).isEqualTo("connect_once");
        assertThat(behavior.objective().successCondition()).isEqualTo("Establish a control link to CORE-01.");
        assertThat(behavior.validation().runtimeExpectation()).isEqualTo("Mission 01 allows the CORE to be connected once.");
        assertThat(behavior.validation().runtimeRetryHint()).isEqualTo("Fix the runtime problem and run the code again.");
        assertThat(behavior.validation().messages()).containsKey("successHeadline");
    }

    @Test
    void loadFailsFastWhenMissionDirectoryIsMissing() {
        assertThatThrownBy(() -> missionBehaviorLoader.load("mission-does-not-exist"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing mission directory");
    }

    @Test
    void parseYamlFailsWhenAllowedCommandsContainDuplicates() {
        final String yaml = """
                version: 1
                missionId: mission-01
                allowedCommands:
                  - Core.connect()
                  - Core.connect()
                execution:
                  temporaryDirectoryPrefix: wake-the-core-
                  resultFileName: wake-the-core-result.properties
                  compilationFailureSummary: failed
                  executionStoppedSummary: stopped
                  initialStatusNoteTemplate: note
                objective:
                  kind: connect_once
                  successCondition: Connect.
                validation:
                  runtimeExpectation: Connect once.
                  runtimeRetryHint: Retry.
                  messages:
                    successHeadline: Success
                """;

        assertThatThrownBy(() -> MissionBehaviorLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("allowedCommands must not contain duplicates");
    }

    @Test
    void parseYamlFailsWhenAllowedRuntimeCommandsContainDuplicates() {
        final String yaml = """
                version: 1
                missionId: mission-01
                allowedCommands:
                  - Core.connect()
                allowedRuntimeCommands:
                  - connect
                  - connect
                execution:
                  temporaryDirectoryPrefix: wake-the-core-
                  resultFileName: wake-the-core-result.properties
                  compilationFailureSummary: failed
                  executionStoppedSummary: stopped
                  initialStatusNoteTemplate: note
                objective:
                  kind: connect_once
                  successCondition: Connect.
                validation:
                  runtimeExpectation: Connect once.
                  runtimeRetryHint: Retry.
                  messages:
                    successHeadline: Success
                """;

        assertThatThrownBy(() -> MissionBehaviorLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("allowedRuntimeCommands must not contain duplicates");
    }

    @Test
    void parseYamlAllowsMissingAllowedRuntimeCommands() {
        final String yaml = """
                version: 1
                missionId: mission-01
                allowedCommands:
                  - Core.connect()
                execution:
                  temporaryDirectoryPrefix: wake-the-core-
                  resultFileName: wake-the-core-result.properties
                  compilationFailureSummary: failed
                  executionStoppedSummary: stopped
                  initialStatusNoteTemplate: note
                objective:
                  kind: connect_once
                  successCondition: Connect.
                validation:
                  runtimeExpectation: Connect once.
                  runtimeRetryHint: Retry.
                  messages:
                    successHeadline: Success
                """;

        final MissionBehaviorConfig behavior = MissionBehaviorLoader.parseYaml(yaml, "inline");

        assertThat(behavior.allowedRuntimeCommands()).isEmpty();
    }

    @Test
    void parseYamlFailsWhenExecutionSectionIsMissing() {
        final String yaml = """
                version: 1
                missionId: mission-01
                allowedCommands:
                  - Core.connect()
                objective:
                  kind: connect_once
                  successCondition: Connect.
                validation:
                  runtimeExpectation: Connect once.
                  runtimeRetryHint: Retry.
                  messages:
                    successHeadline: Success
                """;

        assertThatThrownBy(() -> MissionBehaviorLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("'execution' must be a mapping");
    }

    @Test
    void parseYamlForMissionFailsWhenMissionIdDoesNotMatch() {
        final String yaml = """
                version: 1
                missionId: mission-02
                allowedCommands:
                  - Core.connect()
                execution:
                  temporaryDirectoryPrefix: wake-the-core-
                  resultFileName: wake-the-core-result.properties
                  compilationFailureSummary: failed
                  executionStoppedSummary: stopped
                  initialStatusNoteTemplate: note
                objective:
                  kind: connect_once
                  successCondition: Connect.
                validation:
                  runtimeExpectation: Connect once.
                  runtimeRetryHint: Retry.
                  messages:
                    successHeadline: Success
                """;

        assertThatThrownBy(() -> MissionBehaviorLoader.parseYamlForMission(yaml, "inline", "mission-01"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not match requested mission");
    }
}
