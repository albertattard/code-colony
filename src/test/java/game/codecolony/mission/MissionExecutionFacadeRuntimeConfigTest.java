package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;

class MissionExecutionFacadeRuntimeConfigTest {

    private final MissionExecutionFacade missionExecutionFacade = new MissionExecutionFacade();
    private final MissionMapLoader missionMapLoader = new MissionMapLoader();

    @Test
    void runtimeConfigIsPreferredWhenPresent() {
        final MissionMap missionMap = missionMapLoader.load("mission-01");
        final MissionMapSpawn coreSpawn = missionMap.requireCoreSpawn("core_01");
        final MissionBehaviorConfig behavior = MissionBehaviorLoader.parseYaml("""
                version: 1
                missionId: mission-01
                allowedCommands:
                  - Core.connect()
                allowedRuntimeCommands:
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
                runtime:
                  worker: generic-mission-worker
                  simulator: generic-mission-simulator
                  initialStatus:
                    mode: withTelemetry
                    state: Connected
                    position: "{coreSpawn.at}"
                    noteTemplate: "Move to {repairPosition}."
                  args:
                    - name: objectiveKind
                      value: "{objective.kind}"
                    - name: validationPayload
                      value: "{validationPayload.base64}"
                    - name: allowedRuntimeCommands
                      value: "{allowedRuntimeCommands.csv}"
                    - name: startPosition
                      value: "{coreSpawn.at}"
                """, "inline");
        final MissionExecutionConfigFactory.MissionExecutionContext context =
                new MissionExecutionConfigFactory.MissionExecutionContext("mission-01", behavior, missionMap, coreSpawn);

        final MissionExecutionConfig config = missionExecutionFacade.configForContext(context);

        assertThat(config.workerClass()).isEqualTo(GenericMissionWorker.class);
        assertThat(config.workerArguments()).hasSize(4);
        assertThat(config.workerArguments().get(0)).isEqualTo("connect_once");
        assertThat(config.workerArguments().get(2)).isEqualTo("connect");
        assertThat(config.workerArguments().get(3)).isEqualTo(coreSpawn.at());
        final String decodedValidationPayload = new String(
                Base64.getDecoder().decode(config.workerArguments().get(1)),
                StandardCharsets.UTF_8
        );
        assertThat(decodedValidationPayload).contains("runtimeExpectation=Connect once.");
        assertThat(config.missionInitialStatus().state()).isEqualTo("Offline");
        assertThat(config.missionInitialStatus().dock()).isEqualTo("Connected");
        assertThat(config.missionInitialStatus().position()).isEqualTo(coreSpawn.at());
        assertThat(config.missionInitialStatus().note()).isEqualTo("Move to B3.");
    }

    @Test
    void fallbackProfileIsUsedWhenRuntimeConfigIsMissing() {
        final MissionMap missionMap = missionMapLoader.load("mission-03");
        final MissionMapSpawn coreSpawn = missionMap.requireCoreSpawn("core_01");
        final MissionBehaviorConfig behavior = MissionBehaviorLoader.parseYaml("""
                version: 1
                missionId: mission-03
                allowedCommands:
                  - Core.connect()
                execution:
                  temporaryDirectoryPrefix: repair-the-core-
                  resultFileName: repair-the-core-result.properties
                  compilationFailureSummary: failed
                  executionStoppedSummary: stopped
                  initialStatusNoteTemplate: note
                objective:
                  kind: repair_to_full
                  successCondition: Repair.
                validation:
                  runtimeExpectation: Repair.
                  runtimeRetryHint: Retry.
                  messages:
                    successHeadline: Success
                """, "inline");
        final MissionExecutionConfigFactory.MissionExecutionContext context =
                new MissionExecutionConfigFactory.MissionExecutionContext("mission-03", behavior, missionMap, coreSpawn);

        final MissionExecutionConfig config = missionExecutionFacade.configForContext(context);

        assertThat(config.workerClass()).isEqualTo(GenericMissionWorker.class);
        assertThat(config.workerArguments().getFirst()).isEqualTo("repair_to_full");
    }

    @Test
    void unknownRuntimePlaceholderFailsFast() {
        final MissionMap missionMap = missionMapLoader.load("mission-01");
        final MissionMapSpawn coreSpawn = missionMap.requireCoreSpawn("core_01");
        final MissionBehaviorConfig behavior = MissionBehaviorLoader.parseYaml("""
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
                runtime:
                  worker: generic-mission-worker
                  simulator: generic-mission-simulator
                  initialStatus:
                    mode: withoutTelemetry
                    noteTemplate: note
                  args:
                    - name: objectiveKind
                      value: "{unknown.token}"
                """, "inline");
        final MissionExecutionConfigFactory.MissionExecutionContext context =
                new MissionExecutionConfigFactory.MissionExecutionContext("mission-01", behavior, missionMap, coreSpawn);

        assertThatThrownBy(() -> missionExecutionFacade.configForContext(context))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("unknown.token")
                .hasMessageContaining("mission-01");
    }
}
