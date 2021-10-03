package org.arkngbot.commandprocessors;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.arkngbot.commandprocessors.impl.VersionCommandProcessor;
import org.arkngbot.services.impl.PropertiesSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VersionCommandProcessorTest {

    private static final String VERSION_MESSAGE = "My current version is x.y.";
    private static final String VERSION_PROPERTY_KEY = "arkngbot.version";
    private static final String VERSION_COMMAND = "version";
    private static final String CURRENT_VERSION = "x.y";
    private static final String VERSION_COMMAND_DESCRIPTION = "See Arkng's current version";


    private CommandProcessor commandProcessor;

    private PropertiesSupport propertiesSupport;

    @BeforeEach
    public void setUp() {
        propertiesSupport = mock(PropertiesSupport.class);

        commandProcessor = new VersionCommandProcessor(propertiesSupport);
    }

    @Test
    public void shouldProcessNameCommand() {
        when(propertiesSupport.getProperty(VERSION_PROPERTY_KEY)).thenReturn(CURRENT_VERSION);

        String message = commandProcessor.processCommand(mock(ApplicationCommandInteractionOption.class));

        assertThat(message, is(VERSION_MESSAGE));
    }

    @Test
    public void shouldSupportCommand() {
        boolean supports = commandProcessor.supports(VERSION_COMMAND);

        assertThat(supports, is(true));
    }

    @Test
    public void shouldNotSupportCommand() {
        boolean supports = commandProcessor.supports(CURRENT_VERSION);

        assertThat(supports, is(false));
    }

    @Test
    public void shouldBuildRequest() {
        ApplicationCommandOptionData request = commandProcessor.buildRequest();

        assertThat(request.name(), is(VERSION_COMMAND));
        assertThat(request.description(), is(VERSION_COMMAND_DESCRIPTION));
        assertThat(request.type(), is(ApplicationCommandOption.Type.SUB_COMMAND.getValue()));
    }
}
