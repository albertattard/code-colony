package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class CompilerFeedbackParserTest {

    @Test
    void parseMapsCompilerLineToLearnerLine() {
        final String compilerOutput = """
                /tmp/PlayerProgram.java:10: error: cannot find symbol
                symbol:   variable core
                location: class game.codecolony.player.PlayerProgram
                """;

        final List<String> feedback = CompilerFeedbackParser.parse(compilerOutput);

        assertThat(feedback).containsExactly("Line 1: cannot find symbol");
    }

    @Test
    void parseFallsBackToTrimmedCompilerLinesWhenPatternDoesNotMatch() {
        final String compilerOutput = """
                random error line
                another detail
                """;

        final List<String> feedback = CompilerFeedbackParser.parse(compilerOutput);

        assertThat(feedback).containsExactly("random error line", "another detail");
    }
}
