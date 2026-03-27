package game.codecolony.mission;

import game.codecolony.runtime.MissionExecutionException;
import game.codecolony.runtime.MissionSimulator;
import game.codecolony.student.Core;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Supplier;

public final class MissionWorkerRunner {

    private static final int MAX_CAPTURED_OUTPUT_LENGTH = 4_000;

    private MissionWorkerRunner() {
    }

    public static <T> void run(final String[] args,
                               final MissionSimulator simulator,
                               final Supplier<T> simulationResultSupplier,
                               final MissionResultValidator<T> validator) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected one argument: result file path");
        }

        final Path resultFile = Path.of(args[0]);
        Core.attachSimulator(simulator);

        final ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
        final PrintStream originalOut = System.out;
        final PrintStream originalErr = System.err;

        MissionRunResult runResult;
        try (PrintStream capturedOut = new PrintStream(stdoutBuffer, true, StandardCharsets.UTF_8);
             PrintStream capturedErr = new PrintStream(stderrBuffer, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);
            System.setErr(capturedErr);
            invokePlayerProgram();
            runResult = validator.validate(simulationResultSupplier.get(), null,
                    normalizeCapturedOutput(stdoutBuffer),
                    normalizeCapturedOutput(stderrBuffer));
        } catch (final InvocationTargetException e) {
            runResult = validator.validate(simulationResultSupplier.get(), runtimeMessageFor(e.getCause()),
                    normalizeCapturedOutput(stdoutBuffer),
                    normalizeCapturedOutput(stderrBuffer));
        } catch (final Throwable e) {
            runResult = validator.validate(simulationResultSupplier.get(), runtimeMessageFor(e),
                    normalizeCapturedOutput(stdoutBuffer),
                    normalizeCapturedOutput(stderrBuffer));
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
            Core.detachSimulator();
        }

        MissionRunResultFileCodec.write(resultFile, runResult);
    }

    private static void invokePlayerProgram() throws Exception {
        final Class<?> playerProgramClass = Class.forName("game.codecolony.player.PlayerProgram");
        final Method runMethod = playerProgramClass.getMethod("run");
        runMethod.invoke(null);
    }

    private static String runtimeMessageFor(final Throwable throwable) {
        if (throwable instanceof MissionExecutionException missionExecutionException) {
            return missionExecutionException.getMessage();
        }

        final String message = throwable.getMessage();
        if (message == null || message.isBlank()) {
            return "The program failed while running.";
        }

        return message;
    }

    private static String normalizeCapturedOutput(final ByteArrayOutputStream outputBuffer) {
        final String output = outputBuffer.toString(StandardCharsets.UTF_8).stripTrailing();
        if (output.length() <= MAX_CAPTURED_OUTPUT_LENGTH) {
            return output;
        }

        return output.substring(0, MAX_CAPTURED_OUTPUT_LENGTH)
                + System.lineSeparator()
                + "... output truncated ...";
    }
}
