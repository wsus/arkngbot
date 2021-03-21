package org.arkngbot.processors;

import org.arkngbot.processors.impl.HelpCommandProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HelpCommandProcessorTest {

    private static final String HELP_TEXT = "Available commands:"
            + "/arkng wiki <query>`: Searches the UESP wiki and returns top five results. If you provide the namespace resulting in a direct hit (for example `/arkng wiki online:divayth fyr`), a direct link to the result will be returned."
            + "/arkng wisdom`: Returns a random paragraph from a random TES lorebook."
            + "/arkng ttc search <query>`: First tries to determine the item from the query using TTC's autocompletion feature, then returns a link to the search results of that item."
            + "/arkng ttc price <query>`: First tries to determine the item from the query using TTC's autocompletion feature, then returns a price check for that item."
            + "/arkng help`: Returns the list of all commands.";
    private static final String HELP = "help";
    private static final String OTHER = "other";

    private CommandProcessor commandProcessor;

    @BeforeEach
    public void setUp() {
        commandProcessor = new HelpCommandProcessor();
    }

    @Test
    public void shouldReturnHelpMessage()  {
        String result = commandProcessor.processCommand(Collections.emptyList());

        assertThat(result, is(HELP_TEXT));
    }

    @Test
    public void shouldSupportCommand() {
        boolean supports = commandProcessor.supports(HELP);

        assertThat(supports, is(true));
    }

    @Test
    public void shouldNotSupportCommand() {
        boolean supports = commandProcessor.supports(OTHER);

        assertThat(supports, is(false));
    }
}
