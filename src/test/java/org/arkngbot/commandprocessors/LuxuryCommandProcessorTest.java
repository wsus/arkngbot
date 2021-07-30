package org.arkngbot.commandprocessors;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.rest.util.ApplicationCommandOptionType;
import org.arkngbot.commandprocessors.impl.LuxuryCommandProcessor;
import org.arkngbot.services.ESOHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LuxuryCommandProcessorTest {

    private static final String LUXURY_COMMAND = "luxury";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the luxury furnishings :frowning:";
    private static final String LUXURY_COMMAND_DESCRIPTION = "See Zanil Theran's current luxury furnishings";
    private static final String LUXURY_FURNISHINGS_INFO = "This is luxury furnisher information.";

    private CommandProcessor commandProcessor;

    private ESOHubService esoHubService;

    @BeforeEach
    public void setUp() {
        esoHubService = mock(ESOHubService.class);
        commandProcessor = new LuxuryCommandProcessor(esoHubService);
    }

    @Test
    public void shouldReturnInformation() throws Exception {
        when(esoHubService.checkLuxuryFurnishings()).thenReturn(LUXURY_FURNISHINGS_INFO);

        String result = commandProcessor.processCommand(mock(ApplicationCommandInteractionOption.class));

        assertThat(result, is(LUXURY_FURNISHINGS_INFO));
    }

    @Test
    public void shouldNotReturnInformationExceptionCaught() throws Exception {
        when(esoHubService.checkLuxuryFurnishings()).thenThrow(new IOException());

        String result = commandProcessor.processCommand(mock(ApplicationCommandInteractionOption.class));

        assertThat(result, is(ERROR_MESSAGE));
    }

    @Test
    public void shouldSupportCommand() {
        boolean supports = commandProcessor.supports(LUXURY_COMMAND);

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

        assertThat(request.name(), is(LUXURY_COMMAND));
        assertThat(request.description(), is(LUXURY_COMMAND_DESCRIPTION));
        assertThat(request.type(), is(ApplicationCommandOptionType.SUB_COMMAND.getValue()));
    }
}
