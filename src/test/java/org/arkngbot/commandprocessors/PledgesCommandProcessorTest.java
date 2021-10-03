package org.arkngbot.commandprocessors;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.arkngbot.commandprocessors.impl.PledgesCommandProcessor;
import org.arkngbot.services.ESOHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PledgesCommandProcessorTest {

    private static final String PLEDGES_COMMAND = "pledges";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the pledges :frowning:";
    private static final String PLEDGES_COMMAND_DESCRIPTION = "See today's Undaunted pledges";
    private static final String PLEDGES_INFO = "This is information about pledges.";

    private CommandProcessor commandProcessor;

    private ESOHubService esoHubService;

    @BeforeEach
    public void setUp() {
        esoHubService = mock(ESOHubService.class);
        commandProcessor = new PledgesCommandProcessor(esoHubService);
    }

    @Test
    public void shouldReturnPledges() throws Exception {
        when(esoHubService.checkPledges()).thenReturn(PLEDGES_INFO);

        String result = commandProcessor.processCommand(mock(ApplicationCommandInteractionOption.class));

        assertThat(result, is(PLEDGES_INFO));
    }

    @Test
    public void shouldNotReturnPledgesExceptionCaught() throws Exception {
        when(esoHubService.checkPledges()).thenThrow(new IOException());

        String result = commandProcessor.processCommand(mock(ApplicationCommandInteractionOption.class));

        assertThat(result, is(ERROR_MESSAGE));
    }

    @Test
    public void shouldSupportCommand() {
        boolean supports = commandProcessor.supports(PLEDGES_COMMAND);

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

        assertThat(request.name(), is(PLEDGES_COMMAND));
        assertThat(request.description(), is(PLEDGES_COMMAND_DESCRIPTION));
        assertThat(request.type(), is(ApplicationCommandOption.Type.SUB_COMMAND.getValue()));
    }
}
