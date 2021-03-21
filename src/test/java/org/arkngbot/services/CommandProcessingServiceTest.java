package org.arkngbot.services;

import org.arkngbot.processors.CommandProcessor;
import org.arkngbot.services.impl.CommandProcessingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class CommandProcessingServiceTest {

    private static final String UNKNOWN_COMMAND = "I don't know such a command :frowning:\nType `/arkng help` for a list of all available commands.";
    private static final String WIKI = "wiki";
    private static final String QUERY = "query";
    private static final String QUERY_PART_2 = "querypart2";
    private static final String PROCESSED_RESULT = "processed result";
    private static final String FULL_COMMAND_WIKI = "/arkng wiki query querypart2";
    private static final String FULL_COMMAND_WISDOM = "/arkng wisdom";

    private CommandProcessingService commandProcessingService;

    @BeforeEach
    public void setUp() {
        CommandProcessor commandProcessorMock = Mockito.mock(CommandProcessor.class);
        commandProcessingService = new CommandProcessingServiceImpl(Collections.singletonList(commandProcessorMock));
        when(commandProcessorMock.supports(WIKI)).thenReturn(true);
        when(commandProcessorMock.processCommand(Arrays.asList(QUERY, QUERY_PART_2))).thenReturn(PROCESSED_RESULT);
    }

    @Test
    public void shouldProcessCommandProcessorFound() {
        String result = commandProcessingService.processCommand(FULL_COMMAND_WIKI);

        assertThat(result, is(PROCESSED_RESULT));
    }

    @Test
    public void shouldNotProcessCommandProcessorNotFound() {
        String result = commandProcessingService.processCommand(FULL_COMMAND_WISDOM);

        assertThat(result, is(UNKNOWN_COMMAND));
    }
}
