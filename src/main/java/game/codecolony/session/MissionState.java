package game.codecolony.session;

final class MissionState {

    private String startCode;
    private String currentCode;
    private boolean completed;

    String startCode() {
        return startCode;
    }

    String currentCode() {
        return currentCode;
    }

    boolean completed() {
        return completed;
    }

    void initializeStartCodeIfMissing(final String code) {
        if (startCode == null) {
            startCode = code;
        }
        if (currentCode == null) {
            currentCode = startCode;
        }
    }

    void setCurrentCode(final String code) {
        currentCode = code;
    }

    void resetCurrentCodeToStartCode(final String fallbackStartCode) {
        if (startCode == null) {
            startCode = fallbackStartCode;
        }
        currentCode = startCode;
    }

    void markSuccessful() {
        completed = true;
    }
}
