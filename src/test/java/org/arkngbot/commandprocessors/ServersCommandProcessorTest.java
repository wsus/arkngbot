package org.arkngbot.commandprocessors;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.arkngbot.commandprocessors.impl.ServersCommandProcessor;
import org.arkngbot.services.ESOHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServersCommandProcessorTest {

    private static final String SERVERS_COMMAND = "servers";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the server status :frowning:";
    private static final String SERVERS_COMMAND_DESCRIPTION = "Check the status of the megaservers";
    private static final String SERVERS_INFO = "This is the server status information.";

    private CommandProcessor commandProcessor;

    private ESOHubService esoHubService;

    @BeforeEach
    public void setUp() {
        esoHubService = mock(ESOHubService.class);
        commandProcessor = new ServersCommandProcessor(esoHubService);
    }

    @Test
    public void shouldReturnInformation() throws Exception {
        when(esoHubService.checkServers()).thenReturn(SERVERS_INFO);

        String result = commandProcessor.processCommand(mock(ApplicationCommandInteractionOption.class));

        assertThat(result, is(SERVERS_INFO));
    }

    @Test
    public void shouldNotReturnInformationExceptionCaught() throws Exception {
        when(esoHubService.checkServers()).thenThrow(new IOException());

        String result = commandProcessor.processCommand(mock(ApplicationCommandInteractionOption.class));

        assertThat(result, is(ERROR_MESSAGE));
    }

    @Test
    public void shouldSupportCommand() {
        boolean supports = commandProcessor.supports(SERVERS_COMMAND);

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

        assertThat(request.name(), is(SERVERS_COMMAND));
        assertThat(request.description(), is(SERVERS_COMMAND_DESCRIPTION));
        assertThat(request.type(), is(ApplicationCommandOption.Type.SUB_COMMAND.getValue()));
    }
}
