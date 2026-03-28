package game.codecolony.mission;

import java.util.ArrayList;
import java.util.List;

public final class MissionExecutionConfig {

    private final String temporaryDirectoryPrefix;
    private final String resultFileName;
    private final Class<?> workerClass;
    private final String compilationFailureSummary;
    private final String executionStoppedSummary;
    private final MissionCoreStatus missionInitialStatus;
    private final List<Class<?>> missionSupportClasses;
    private final List<String> workerArguments;

    private MissionExecutionConfig(final Builder builder) {
        this.temporaryDirectoryPrefix = requireNonBlank(builder.temporaryDirectoryPrefix, "temporaryDirectoryPrefix");
        this.resultFileName = requireNonBlank(builder.resultFileName, "resultFileName");
        this.workerClass = requireNonNull(builder.workerClass, "workerClass");
        this.compilationFailureSummary = requireNonBlank(builder.compilationFailureSummary, "compilationFailureSummary");
        this.executionStoppedSummary = requireNonBlank(builder.executionStoppedSummary, "executionStoppedSummary");
        if (builder.missionInitialStatus == null) {
            throw new IllegalArgumentException("missionInitialStatus is required");
        }
        this.missionInitialStatus = builder.missionInitialStatus;

        if (builder.missionSupportClasses.isEmpty()) {
            throw new IllegalArgumentException("missionSupportClasses must not be empty");
        }
        this.missionSupportClasses = List.copyOf(builder.missionSupportClasses);
        this.workerArguments = List.copyOf(builder.workerArguments);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String temporaryDirectoryPrefix() {
        return temporaryDirectoryPrefix;
    }

    public String resultFileName() {
        return resultFileName;
    }

    public Class<?> workerClass() {
        return workerClass;
    }

    public String compilationFailureSummary() {
        return compilationFailureSummary;
    }

    public String executionStoppedSummary() {
        return executionStoppedSummary;
    }

    public MissionCoreStatus missionInitialStatus() {
        return missionInitialStatus;
    }

    public List<Class<?>> missionSupportClasses() {
        return missionSupportClasses;
    }

    public List<String> workerArguments() {
        return workerArguments;
    }

    public static final class Builder {

        private String temporaryDirectoryPrefix;
        private String resultFileName;
        private Class<?> workerClass;
        private String compilationFailureSummary;
        private String executionStoppedSummary;
        private MissionCoreStatus missionInitialStatus;
        private final List<Class<?>> missionSupportClasses = new ArrayList<>();
        private final List<String> workerArguments = new ArrayList<>();

        private Builder() {
        }

        public Builder temporaryDirectoryPrefix(final String value) {
            this.temporaryDirectoryPrefix = value;
            return this;
        }

        public Builder resultFileName(final String value) {
            this.resultFileName = value;
            return this;
        }

        public Builder workerClass(final Class<?> value) {
            this.workerClass = value;
            return this;
        }

        public Builder compilationFailureSummary(final String value) {
            this.compilationFailureSummary = value;
            return this;
        }

        public Builder executionStoppedSummary(final String value) {
            this.executionStoppedSummary = value;
            return this;
        }

        public Builder missionInitialStatus(final MissionCoreStatus value) {
            this.missionInitialStatus = value;
            return this;
        }

        public Builder missionSupportClasses(final List<Class<?>> value) {
            this.missionSupportClasses.clear();
            if (value != null) {
                this.missionSupportClasses.addAll(value);
            }
            return this;
        }

        public Builder workerArguments(final List<String> value) {
            this.workerArguments.clear();
            if (value != null) {
                this.workerArguments.addAll(value);
            }
            return this;
        }

        public MissionExecutionConfig build() {
            return new MissionExecutionConfig(this);
        }
    }

    private static String requireNonBlank(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value;
    }

    private static <T> T requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value;
    }
}
