package game.codecolony.mission;

import game.codecolony.runtime.MissionExecutionException;
import game.codecolony.student.CORE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

public final class WakeTheCoreMissionWorker {

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

        WakeTheCoreRunResult runResult;
        try {
            invokePlayerProgram();
            runResult = validator.validate(simulator.finish(), null);
        } catch (InvocationTargetException invocationTargetException) {
            final Throwable cause = invocationTargetException.getCause();
            final String runtimeError = runtimeMessageFor(cause);
            runResult = validator.validate(simulator.finish(), runtimeError);
        } catch (Throwable throwable) {
            runResult = validator.validate(simulator.finish(), runtimeMessageFor(throwable));
        } finally {
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
}
