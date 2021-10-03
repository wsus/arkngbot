package org.arkngbot.commandprocessors;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.arkngbot.commandprocessors.impl.GoldenCommandProcessor;
import org.arkngbot.services.ESOHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoldenCommandProcessorTest {

    private static final String GOLDEN_COMMAND = "golden";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the golden items :frowning:";
    private static final String GOLDEN_COMMAND_DESCRIPTION = "Check out Adhazabi Aba-daro's golden items";
    private static final String GOLDEN_INFO = "This is golden merchant information.";

    private CommandProcessor commandProcessor;

    private ESOHubService esoHubService;

    @BeforeEach
    public void setUp() {
        esoHubService = mock(ESOHubService.class);
        commandProcessor = new GoldenCommandProcessor(esoHubService);
    }

    @Test
    public void shouldReturnInformation() throws Exception {
        when(esoHubService.checkGoldenItems()).thenReturn(GOLDEN_INFO);

        String result = commandProcessor.processCommand(mock(ApplicationCommandInteractionOption.class));

        assertThat(result, is(GOLDEN_INFO));
    }

    @Test
    public void shouldNotReturnInformationExceptionCaught() throws Exception {
        when(esoHubService.checkGoldenItems()).thenThrow(new IOException());

        String result = commandProcessor.processCommand(mock(ApplicationCommandInteractionOption.class));

        assertThat(result, is(ERROR_MESSAGE));
    }

    @Test
    public void shouldSupportCommand() {
        boolean supports = commandProcessor.supports(GOLDEN_COMMAND);

        assertThat(supports, is(true));
    }

    @Test
    public void shouldNotSupportCommand() {
        boolean supports = commandProcessor.supports(ERROR_MESSAGE);

        assertThat(supports, is(false));
    }

    @Test
    public void shouldBuildRequest() {
        ApplicationCommandOptionData request = commandProcessor.buildRequest();

        assertThat(request.name(), is(GOLDEN_COMMAND));
        assertThat(request.description(), is(GOLDEN_COMMAND_DESCRIPTION));
        assertThat(request.type(), is(ApplicationCommandOption.Type.SUB_COMMAND.getValue()));
    }
}
