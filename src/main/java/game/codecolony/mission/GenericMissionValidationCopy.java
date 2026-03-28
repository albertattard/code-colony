package game.codecolony.mission;

import java.util.Map;

record GenericMissionValidationCopy(String runtimeExpectation,
                                    String runtimeRetryHint,
                                    Map<String, String> messages) {

    GenericMissionValidationCopy {
        messages = Map.copyOf(messages);
    }

    String requireMessage(final String key) {
        final String message = messages.get(key);
        if (message == null || message.isBlank()) {
            throw new IllegalStateException("Missing validation message key: " + key);
        }
        return message;
    }
}
