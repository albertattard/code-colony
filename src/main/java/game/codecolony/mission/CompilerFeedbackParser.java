package game.codecolony.mission;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CompilerFeedbackParser {

    private static final Pattern COMPILER_ERROR_PATTERN =
            Pattern.compile("(?m)^.*PlayerProgram\\.java:(\\d+): error: (.+)$");
    private static final int WRAPPER_LINE_OFFSET = 9;

    private CompilerFeedbackParser() {
    }

    static List<String> parse(final String compilerOutput) {
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
}
