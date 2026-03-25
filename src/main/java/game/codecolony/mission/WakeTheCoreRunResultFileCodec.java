package game.codecolony.mission;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

final class WakeTheCoreRunResultFileCodec {

    private WakeTheCoreRunResultFileCodec() {
    }

    static WakeTheCoreRunResult read(final Path path) throws IOException {
        final Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(path)) {
            properties.load(inputStream);
        }

        return new WakeTheCoreRunResult(
                properties.getProperty("headline"),
                properties.getProperty("summary"),
                readList(properties, "simulationEvents"),
                readList(properties, "feedbackItems"),
                new WakeTheCoreCoreStatus(
                        properties.getProperty("coreStatus.state"),
                        properties.getProperty("coreStatus.battery"),
                        properties.getProperty("coreStatus.dock"),
                        properties.getProperty("coreStatus.position"),
                        properties.getProperty("coreStatus.note")
                ),
                Boolean.parseBoolean(properties.getProperty("success"))
        );
    }

    static void write(final Path path, final WakeTheCoreRunResult runResult) throws IOException {
        final Properties properties = new Properties();
        properties.setProperty("headline", runResult.headline());
        properties.setProperty("summary", runResult.summary());
        writeList(properties, "simulationEvents", runResult.simulationEvents());
        writeList(properties, "feedbackItems", runResult.feedbackItems());
        properties.setProperty("coreStatus.state", runResult.coreStatus().state());
        properties.setProperty("coreStatus.battery", runResult.coreStatus().battery());
        properties.setProperty("coreStatus.dock", runResult.coreStatus().dock());
        properties.setProperty("coreStatus.position", runResult.coreStatus().position());
        properties.setProperty("coreStatus.note", runResult.coreStatus().note());
        properties.setProperty("success", Boolean.toString(runResult.success()));

        try (OutputStream outputStream = Files.newOutputStream(path)) {
            properties.store(outputStream, "Wake The CORE run result");
        }
    }

    private static List<String> readList(final Properties properties, final String prefix) {
        final int count = Integer.parseInt(properties.getProperty(prefix + ".count", "0"));
        final List<String> items = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            items.add(properties.getProperty(prefix + "." + index));
        }

        return List.copyOf(items);
    }

    private static void writeList(final Properties properties, final String prefix, final List<String> items) {
        properties.setProperty(prefix + ".count", Integer.toString(items.size()));
        for (int index = 0; index < items.size(); index++) {
            properties.setProperty(prefix + "." + index, items.get(index));
        }
    }
}
