package game.codecolony.mission;

import game.codecolony.runtime.ChargeCoreCommand;
import game.codecolony.runtime.ChargeCoreResult;
import game.codecolony.runtime.ConnectCoreResult;
import game.codecolony.runtime.ConnectNextCoreCommand;
import game.codecolony.runtime.ConnectionRejectedEvent;
import game.codecolony.runtime.CoreChargeCappedEvent;
import game.codecolony.runtime.CoreChargedEvent;
import game.codecolony.runtime.CoreConnectedEvent;
import game.codecolony.runtime.CoreMovedEvent;
import game.codecolony.runtime.CoreRepairedEvent;
import game.codecolony.runtime.MissionCommand;
import game.codecolony.runtime.MissionCommandResult;
import game.codecolony.runtime.MissionEvent;
import game.codecolony.runtime.MissionExecutionException;
import game.codecolony.runtime.MissionSimulator;
import game.codecolony.runtime.MoveCoreCommand;
import game.codecolony.runtime.MoveCoreResult;
import game.codecolony.runtime.RepairCoreCommand;
import game.codecolony.runtime.RepairCoreResult;
import game.codecolony.student.Core;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public final class ChargeTheCoreMissionExecutionService {

    private static final String PLAYER_PROGRAM_SOURCE = "PlayerProgram.java";
    private static final String RESULT_FILE = "charge-the-core-result.properties";
    private static final String SUPPORT_CLASSES_DIRECTORY = "support-classes";
    private static final Duration COMPILE_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration RUN_TIMEOUT = Duration.ofSeconds(5);
    private static final Pattern COMPILER_ERROR_PATTERN =
            Pattern.compile("(?m)^.*PlayerProgram\\.java:(\\d+): error: (.+)$");
    private static final int WRAPPER_LINE_OFFSET = 9;

    public MissionRunResult execute(final String code) {
        Path workingDirectory = null;
        try {
            workingDirectory = Files.createTempDirectory("charge-the-core-");
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
                return compilationFailure(compilation.combinedOutput());
            }

            final Path resultFile = workingDirectory.resolve(RESULT_FILE);
            final ProcessResult execution = runProcess(
                    List.of(
                            javaTool("java").toString(),
                            "-cp", classesDirectory + System.getProperty("path.separator") + supportClassesDirectory,
                            "game.codecolony.mission.ChargeTheCoreMissionWorker",
                            resultFile.toString()
                    ),
                    RUN_TIMEOUT
            );

            if (Files.exists(resultFile)) {
                return MissionRunResultFileCodec.read(resultFile);
            }

            return executionFailure(execution.combinedOutput());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return executionFailure("The mission worker could not be started.");
        } catch (IOException ioException) {
            return executionFailure("The mission worker could not be started.");
        } finally {
            deleteQuietly(workingDirectory);
        }
    }

    private MissionRunResult compilationFailure(final String compilerOutput) {
        final List<String> feedbackItems = parseCompilerFeedback(compilerOutput);
        return new MissionRunResult(
                "Compilation Failed",
                "The code could not be compiled for Mission 02.",
                List.of("Compilation stopped before the mission could run."),
                feedbackItems,
                missionTwoInitialStatus(),
                "",
                "",
                false
        );
    }

    private MissionRunResult executionFailure(final String processOutput) {
        return new MissionRunResult(
                "Run Failed",
                "The mission worker did not return a valid result.",
                List.of("Execution stopped before Mission 02 could be evaluated."),
                List.of(processOutput == null || processOutput.isBlank()
                        ? "No runtime diagnostics were returned."
                        : processOutput.strip()),
                missionTwoInitialStatus(),
                "",
                "",
                false
        );
    }

    private MissionCoreStatus missionTwoInitialStatus() {
        return new MissionCoreStatus("CORE-01", "Online", 0, 5, 1, 5, "Connected", "B1",
                "Control link remains stable from Mission 01. Battery depleted. Structural damage still detected.");
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

                import game.codecolony.student.Core;

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
                .start();
        final FutureTask<String> stdoutReader = readStream(process.getInputStream());
        final FutureTask<String> stderrReader = readStream(process.getErrorStream());
        Thread.ofVirtual().start(stdoutReader);
        Thread.ofVirtual().start(stderrReader);
        final boolean finished = process.waitFor(timeout.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
        if (!finished) {
            process.destroyForcibly();
            return new ProcessResult(-1, "", "The process timed out.");
        }

        final String stdout = readFuture(stdoutReader);
        final String stderr = readFuture(stderrReader);
        return new ProcessResult(process.exitValue(), stdout, stderr);
    }

    private FutureTask<String> readStream(final InputStream inputStream) {
        return new FutureTask<>(() -> new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
    }

    private String readFuture(final FutureTask<String> futureTask) throws InterruptedException, IOException {
        try {
            return futureTask.get();
        } catch (ExecutionException executionException) {
            throw new IOException("Unable to read process output.", executionException.getCause());
        }
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
                Core.class,
                MissionCommand.class,
                MissionCommandResult.class,
                MissionEvent.class,
                MissionExecutionException.class,
                MissionSimulator.class,
                ChargeCoreCommand.class,
                ChargeCoreResult.class,
                ConnectCoreResult.class,
                ConnectNextCoreCommand.class,
                MoveCoreCommand.class,
                MoveCoreResult.class,
                RepairCoreCommand.class,
                RepairCoreResult.class,
                ConnectionRejectedEvent.class,
                CoreChargeCappedEvent.class,
                CoreChargedEvent.class,
                CoreConnectedEvent.class,
                CoreMovedEvent.class,
                CoreRepairedEvent.class,
                MissionCoreStatus.class,
                MissionRunResult.class,
                MissionRunResultFileCodec.class,
                ChargeTheCoreMissionSimulation.class,
                ChargeTheCoreMissionSimulator.class,
                ChargeTheCoreMissionValidator.class,
                ChargeTheCoreMissionWorker.class
        );
    }

    private record ProcessResult(int exitCode, String stdout, String stderr) {
        private String combinedOutput() {
            final String trimmedStdout = stdout == null ? "" : stdout.strip();
            final String trimmedStderr = stderr == null ? "" : stderr.strip();
            if (trimmedStdout.isEmpty()) {
                return trimmedStderr;
            }
            if (trimmedStderr.isEmpty()) {
                return trimmedStdout;
            }
            return trimmedStdout + System.lineSeparator() + trimmedStderr;
        }
    }
}
