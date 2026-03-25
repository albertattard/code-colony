package game.codecolony.mission;

import game.codecolony.runtime.MissionExecutionException;
import game.codecolony.student.CORE;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public final class WakeTheCoreMissionWorker {

    private static final int MAX_CAPTURED_OUTPUT_LENGTH = 4_000;

    private WakeTheCoreMissionWorker() {
    }

    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected one argument: result file path");
        }

        final Path resultFile = Path.of(args[0]);
        final WakeTheCoreMissionSimulator simulator = new WakeTheCoreMissionSimulator();
        final WakeTheCoreMissionValidator validator = new WakeTheCoreMissionValidator();
        CORE.attachSimulator(simulator);

        final ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
        final PrintStream originalOut = System.out;
        final PrintStream originalErr = System.err;

        WakeTheCoreRunResult runResult;
        try (PrintStream capturedOut = new PrintStream(stdoutBuffer, true, StandardCharsets.UTF_8);
             PrintStream capturedErr = new PrintStream(stderrBuffer, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);
            System.setErr(capturedErr);
            invokePlayerProgram();
            runResult = validator.validate(simulator.finish(), null,
                    normalizeCapturedOutput(stdoutBuffer),
                    normalizeCapturedOutput(stderrBuffer));
        } catch (InvocationTargetException invocationTargetException) {
            final Throwable cause = invocationTargetException.getCause();
            final String runtimeError = runtimeMessageFor(cause);
            runResult = validator.validate(simulator.finish(), runtimeError,
                    normalizeCapturedOutput(stdoutBuffer),
                    normalizeCapturedOutput(stderrBuffer));
        } catch (Throwable throwable) {
            runResult = validator.validate(simulator.finish(), runtimeMessageFor(throwable),
                    normalizeCapturedOutput(stdoutBuffer),
                    normalizeCapturedOutput(stderrBuffer));
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
            CORE.detachSimulator();
        }

        WakeTheCoreRunResultFileCodec.write(resultFile, runResult);
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
