package org.arkngbot.commandprocessors;

import org.arkngbot.commandprocessors.impl.WisdomCommandProcessor;
import org.arkngbot.services.UESPRandomLorebookParagraphExtractorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WisdomCommandProcessorTest {

    private static final String SEARCH_QUERY_WORD_1 = "search";
    private static final String SEARCH_QUERY_WORD_2 = "query";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the wisdom :frowning:";
    private static final String WISDOM_COMMAND = "wisdom";
    private static final String WISDOM = "This is a wisdom";

    private CommandProcessor commandProcessor;

    private UESPRandomLorebookParagraphExtractorService uespRandomLorebookParagraphExtractorService;

    @BeforeEach
    public void setUp() {
        uespRandomLorebookParagraphExtractorService = mock(UESPRandomLorebookParagraphExtractorService.class);
        commandProcessor = new WisdomCommandProcessor(uespRandomLorebookParagraphExtractorService);
    }

    @Test
    public void shouldReturnWisdom() throws Exception {
        when(uespRandomLorebookParagraphExtractorService.extractRandomLorebookParagraph()).thenReturn(WISDOM);

        String result = commandProcessor.processCommand(Collections.emptyList());

        assertThat(result, is(WISDOM));
    }

    @Test
    public void shouldNotReturnWisdomExceptionCaught() throws Exception {
        when(uespRandomLorebookParagraphExtractorService.extractRandomLorebookParagraph()).thenThrow(new IOException());

        String result = commandProcessor.processCommand(Arrays.asList(SEARCH_QUERY_WORD_1, SEARCH_QUERY_WORD_2));

        assertThat(result, is(ERROR_MESSAGE));
    }

    @Test
    public void shouldSupportCommand() {
        boolean supports = commandProcessor.supports(WISDOM_COMMAND);

        assertThat(supports, is(true));
    }

    @Test
    public void shouldNotSupportCommand() {
        boolean supports = commandProcessor.supports(WISDOM);

        assertThat(supports, is(false));
    }
}
