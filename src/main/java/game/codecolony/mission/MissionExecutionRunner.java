package game.codecolony.mission;

import java.io.File;
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
import java.util.stream.Stream;

public final class MissionExecutionRunner {

    private static final String PLAYER_PROGRAM_SOURCE = "PlayerProgram.java";
    private static final String SUPPORT_CLASSES_DIRECTORY = "support-classes";
    private static final Duration COMPILE_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration RUN_TIMEOUT = Duration.ofSeconds(5);
    public MissionRunResult execute(final String code, final MissionExecutionConfig config) {
        Path workingDirectory = null;
        try {
            workingDirectory = Files.createTempDirectory(config.temporaryDirectoryPrefix());
            final Path sourceDirectory = workingDirectory.resolve("src/game/codecolony/player");
            final Path classesDirectory = workingDirectory.resolve("classes");
            final Path supportClassesDirectory = workingDirectory.resolve(SUPPORT_CLASSES_DIRECTORY);
            Files.createDirectories(sourceDirectory);
            Files.createDirectories(classesDirectory);
            prepareSupportClasses(supportClassesDirectory, config);

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
                return compilationFailure(compilation.combinedOutput(), config);
            }

            final Path resultFile = workingDirectory.resolve(config.resultFileName());
            final List<String> executionCommand = new ArrayList<>(List.of(
                    javaTool("java").toString(),
                    "-cp", classesDirectory + File.pathSeparator + supportClassesDirectory,
                    config.workerClassName(),
                    resultFile.toString()
            ));
            executionCommand.addAll(config.workerArguments());

            final ProcessResult execution = runProcess(executionCommand, RUN_TIMEOUT);

            if (Files.exists(resultFile)) {
                return MissionRunResultFileCodec.read(resultFile);
            }

            return executionFailure(execution.combinedOutput(), config);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return executionFailure("The mission worker could not be started.", config);
        } catch (IOException ioException) {
            return executionFailure("The mission worker could not be started.", config);
        } finally {
            deleteQuietly(workingDirectory);
        }
    }

    private MissionRunResult compilationFailure(final String compilerOutput, final MissionExecutionConfig config) {
        final List<String> feedbackItems = CompilerFeedbackParser.parse(compilerOutput);
        return new MissionRunResult(
                "Compilation Failed",
                config.compilationFailureSummary(),
                List.of("Compilation stopped before the mission could run."),
                feedbackItems,
                config.missionInitialStatus(),
                "",
                "",
                false
        );
    }

    private MissionRunResult executionFailure(final String processOutput, final MissionExecutionConfig config) {
        return new MissionRunResult(
                "Run Failed",
                "The mission worker did not return a valid result.",
                List.of(config.executionStoppedSummary()),
                List.of(processOutput == null || processOutput.isBlank()
                        ? "No runtime diagnostics were returned."
                        : processOutput.strip()),
                config.missionInitialStatus(),
                "",
                "",
                false
        );
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
                        } catch (IOException _) {
                        }
                    });
        } catch (IOException _) {
        }
    }

    private void prepareSupportClasses(final Path supportClassesDirectory,
                                       final MissionExecutionConfig config) throws IOException {
        Files.createDirectories(supportClassesDirectory);
        for (final Class<?> supportClass : supportClasses(config)) {
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

    private List<Class<?>> supportClasses(final MissionExecutionConfig config) {
        return Stream.concat(MissionSupportClassCatalog.commonSupportClasses().stream(), config.missionSupportClasses().stream())
                .toList();
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
