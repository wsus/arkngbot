package org.arkngbot.commandprocessors;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.rest.util.ApplicationCommandOptionType;
import org.arkngbot.commandprocessors.impl.WisdomCommandProcessor;
import org.arkngbot.services.UESPRandomLorebookParagraphExtractorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WisdomCommandProcessorTest {

    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the wisdom :frowning:";
    private static final String WISDOM_COMMAND = "wisdom";
    private static final String WISDOM = "This is a wisdom";
    private static final String WISDOM_COMMAND_DESCRIPTION = "Ask Arkng for a random lorebook paragraph";


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

        String result = commandProcessor.processCommand(mock(ApplicationCommandInteractionOption.class));

        assertThat(result, is(WISDOM));
    }

    @Test
    public void shouldNotReturnWisdomExceptionCaught() throws Exception {
        when(uespRandomLorebookParagraphExtractorService.extractRandomLorebookParagraph()).thenThrow(new IOException());

        String result = commandProcessor.processCommand(mock(ApplicationCommandInteractionOption.class));

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

    @Test
    public void shouldBuildRequest() {
        ApplicationCommandOptionData request = commandProcessor.buildRequest();

        assertThat(request.name(), is(WISDOM_COMMAND));
        assertThat(request.description(), is(WISDOM_COMMAND_DESCRIPTION));
        assertThat(request.type(), is(ApplicationCommandOptionType.SUB_COMMAND.getValue()));
    }
}
