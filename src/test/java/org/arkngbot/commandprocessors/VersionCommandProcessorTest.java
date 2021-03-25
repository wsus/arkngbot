package org.arkngbot.commandprocessors;

import org.arkngbot.commandprocessors.impl.VersionCommandProcessor;
import org.arkngbot.services.impl.PropertiesSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VersionCommandProcessorTest {

    private static final String VERSION_MESSAGE = "My current version is x.y.";
    private static final String VERSION_PROPERTY_KEY = "arkngbot.version";
    private static final String VERSION_COMMAND = "version";
    private static final String CURRENT_VERSION = "x.y";

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

        String message = commandProcessor.processCommand(new ArrayList<>());

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
}
