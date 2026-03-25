package game.codecolony.mission;

import game.codecolony.runtime.ConnectCoreResult;
import game.codecolony.runtime.ConnectNextCoreCommand;
import game.codecolony.runtime.ConnectionRejectedEvent;
import game.codecolony.runtime.CoreConnectedEvent;
import game.codecolony.runtime.MissionCommand;
import game.codecolony.runtime.MissionCommandResult;
import game.codecolony.runtime.MissionEvent;
import game.codecolony.runtime.MissionExecutionException;
import game.codecolony.runtime.MissionSimulator;
import game.codecolony.student.CORE;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WakeTheCoreMissionExecutionService {

    private static final String PLAYER_PROGRAM_SOURCE = "PlayerProgram.java";
    private static final String RESULT_FILE = "wake-the-core-result.properties";
    private static final String SUPPORT_CLASSES_DIRECTORY = "support-classes";
    private static final Duration COMPILE_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration RUN_TIMEOUT = Duration.ofSeconds(5);
    private static final Pattern COMPILER_ERROR_PATTERN =
            Pattern.compile("(?m)^.*PlayerProgram\\.java:(\\d+): error: (.+)$");
    private static final int WRAPPER_LINE_OFFSET = 9;

    public WakeTheCoreRunResult execute(final String code) {
        Path workingDirectory = null;
        try {
            workingDirectory = Files.createTempDirectory("wake-the-core-");
            final Path sourceDirectory = workingDirectory.resolve("src/game/codecolony/player");
            final Path classesDirectory = workingDirectory.resolve("classes");
            final Path supportClassesDirectory = workingDirectory.resolve(SUPPORT_CLASSES_DIRECTORY);
            Files.createDirectories(sourceDirectory);
            Files.createDirectories(classesDirectory);
            prepareSupportClasses(supportClassesDirectory);

            final Path sourceFile = sourceDirectory.resolve(PLAYER_PROGRAM_SOURCE);
            Files.writeString(sourceFile, wrapSnippet(code), StandardCharsets.UTF_8);

            final ProcessResult compilation = runProcess(
                    List.of(
                            javaTool("javac").toString(),
                            "-encoding", "UTF-8",
                            "-classpath", supportClassesDirectory.toString(),
                            "-d", classesDirectory.toString(),
                            sourceFile.toString()
                    ),
                    COMPILE_TIMEOUT
            );

            if (compilation.exitCode() != 0) {
                return compilationFailure(compilation.output());
            }

            final Path resultFile = workingDirectory.resolve(RESULT_FILE);
            final ProcessResult execution = runProcess(
                    List.of(
                            javaTool("java").toString(),
                            "-cp", classesDirectory + System.getProperty("path.separator") + supportClassesDirectory,
                            "game.codecolony.mission.WakeTheCoreMissionWorker",
                            resultFile.toString()
                    ),
                    RUN_TIMEOUT
            );

            if (Files.exists(resultFile)) {
                return WakeTheCoreRunResultFileCodec.read(resultFile);
            }

            return executionFailure(execution.output());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return executionFailure("The mission worker could not be started.");
        } catch (IOException ioException) {
            return executionFailure("The mission worker could not be started.");
        } finally {
            deleteQuietly(workingDirectory);
        }
    }

    private WakeTheCoreRunResult compilationFailure(final String compilerOutput) {
        final List<String> feedbackItems = parseCompilerFeedback(compilerOutput);
        return new WakeTheCoreRunResult(
                "Compilation Failed",
                "The code could not be compiled for Mission 01.",
                List.of("Compilation stopped before the mission could run."),
                feedbackItems,
                new WakeTheCoreCoreStatus("CORE-01", "Offline", null, null, null, null, "", "", "No telemetry available while offline."),
                false
        );
    }

    private WakeTheCoreRunResult executionFailure(final String processOutput) {
        return new WakeTheCoreRunResult(
                "Run Failed",
                "The mission worker did not return a valid result.",
                List.of("Execution stopped before Mission 01 could be evaluated."),
                List.of(processOutput == null || processOutput.isBlank()
                        ? "No runtime diagnostics were returned."
                        : processOutput.strip()),
                new WakeTheCoreCoreStatus("CORE-01", "Offline", null, null, null, null, "", "", "No telemetry available while offline."),
                false
        );
    }

    private List<String> parseCompilerFeedback(final String compilerOutput) {
        final Matcher matcher = COMPILER_ERROR_PATTERN.matcher(compilerOutput);
        final List<String> feedback = new ArrayList<>();
        while (matcher.find()) {
            final int sourceLine = Integer.parseInt(matcher.group(1));
            final int learnerLine = Math.max(1, sourceLine - WRAPPER_LINE_OFFSET);
            feedback.add("Line %d: %s".formatted(learnerLine, matcher.group(2)));
        }

        if (!feedback.isEmpty()) {
            return List.copyOf(feedback);
        }

        return compilerOutput.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .map(line -> line.replaceAll("^.+PlayerProgram\\.java:", "Line "))
                .limit(5)
                .toList();
    }

    private String wrapSnippet(final String code) {
        return """
                package game.codecolony.player;

                import game.codecolony.student.CORE;

                public final class PlayerProgram {
                    private PlayerProgram() {
                    }

                    public static void run() {
                %s
                    }
                }
                """.formatted(indentSnippet(code));
    }

    private String indentSnippet(final String code) {
        return code.lines()
                .map(line -> "        " + line)
                .reduce((left, right) -> left + System.lineSeparator() + right)
                .orElse("");
    }

    private ProcessResult runProcess(final List<String> command, final Duration timeout)
            throws IOException, InterruptedException {
        final Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
        final boolean finished = process.waitFor(timeout.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
        if (!finished) {
            process.destroyForcibly();
            return new ProcessResult(-1, "The process timed out.");
        }

        final String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return new ProcessResult(process.exitValue(), output);
    }

    private Path javaTool(final String toolName) {
        final Path javaHome = Path.of(System.getProperty("java.home"));
        final List<Path> candidates = List.of(
                javaHome.resolve("bin").resolve(toolName),
                javaHome.resolve("bin").resolve(toolName + ".exe"),
                javaHome.getParent() != null ? javaHome.getParent().resolve("bin").resolve(toolName) : javaHome.resolve(toolName),
                javaHome.getParent() != null ? javaHome.getParent().resolve("bin").resolve(toolName + ".exe") : javaHome.resolve(toolName + ".exe")
        );

        return candidates.stream()
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unable to locate " + toolName));
    }

    private void deleteQuietly(final Path workingDirectory) {
        if (workingDirectory == null || !Files.exists(workingDirectory)) {
            return;
        }

        try (var walk = Files.walk(workingDirectory)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }

    private void prepareSupportClasses(final Path supportClassesDirectory) throws IOException {
        Files.createDirectories(supportClassesDirectory);
        for (final Class<?> supportClass : supportClasses()) {
            copyClassFile(supportClass, supportClassesDirectory);
        }
    }

    private void copyClassFile(final Class<?> type, final Path supportClassesDirectory) throws IOException {
        final String resourceName = type.getSimpleName() + ".class";
        final Path targetPath = supportClassesDirectory.resolve(type.getName().replace('.', '/') + ".class");
        Files.createDirectories(targetPath.getParent());
        try (InputStream inputStream = type.getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IOException("Unable to load class bytes for " + type.getName());
            }
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private List<Class<?>> supportClasses() {
        return List.of(
                CORE.class,
                MissionCommand.class,
                MissionCommandResult.class,
                MissionEvent.class,
                MissionExecutionException.class,
                MissionSimulator.class,
                ConnectCoreResult.class,
                ConnectNextCoreCommand.class,
                ConnectionRejectedEvent.class,
                CoreConnectedEvent.class,
                WakeTheCoreCoreStatus.class,
                WakeTheCoreRunResult.class,
                WakeTheCoreRunResultFileCodec.class,
                WakeTheCoreMissionSimulation.class,
                WakeTheCoreMissionSimulator.class,
                WakeTheCoreMissionValidator.class,
                WakeTheCoreMissionWorker.class
        );
    }

    private record ProcessResult(int exitCode, String output) {
    }
}
