package org.arkngbot.services;

import discord4j.core.object.command.ApplicationCommandInteraction;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.impl.CommandProcessingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandProcessingServiceTest {

    private static final String UNKNOWN_COMMAND = "I don't know such a command :frowning:\nType `/arkng help` for a list of all available commands.";
    private static final String WIKI = "wiki";
    private static final String PROCESSED_RESULT = "processed result";
    private static final String COMMAND_WIKI = "wiki";
    private static final String COMMAND_WISDOM = "wisdom";
    private static final String COMMAND_ARKNG = "arkng";

    private CommandProcessingService commandProcessingService;
    private CommandProcessor commandProcessorMock;

    @BeforeEach
    public void setUp() {
        commandProcessorMock = mock(CommandProcessor.class);
        commandProcessingService = new CommandProcessingServiceImpl(Collections.singletonList(commandProcessorMock));
        when(commandProcessorMock.supports(WIKI)).thenReturn(true);
    }

    @Test
    public void shouldProcessCommandProcessorFound() {
        ApplicationCommandInteraction acid = mock(ApplicationCommandInteraction.class);
        ApplicationCommandInteractionOption firstOption = mock(ApplicationCommandInteractionOption.class);
        when(acid.getOptions()).thenReturn(Collections.singletonList(firstOption));
        when(firstOption.getName()).thenReturn(COMMAND_WIKI);
        when(commandProcessorMock.processCommand(firstOption)).thenReturn(PROCESSED_RESULT);

        String result = commandProcessingService.processCommand(acid);

        assertThat(result, is(PROCESSED_RESULT));
    }

    @Test
    public void shouldNotProcessCommandProcessorNotFound() {
        ApplicationCommandInteraction acid = mock(ApplicationCommandInteraction.class);
        ApplicationCommandInteractionOption firstOption = mock(ApplicationCommandInteractionOption.class);
        when(acid.getOptions()).thenReturn(Collections.singletonList(firstOption));
        when(firstOption.getName()).thenReturn(COMMAND_WISDOM);
        String result = commandProcessingService.processCommand(acid);

        assertThat(result, is(UNKNOWN_COMMAND));
    }

    @Test
    public void shouldThrowExceptionNoConcreteSubcommand() {
        ApplicationCommandInteraction acid = mock(ApplicationCommandInteraction.class);
        ApplicationCommandInteractionOption firstOption = mock(ApplicationCommandInteractionOption.class);
        when(acid.getOptions()).thenReturn(Collections.emptyList());
        when(firstOption.getName()).thenReturn(COMMAND_WIKI);

        assertThrows(IllegalArgumentException.class, () -> commandProcessingService.processCommand(acid));
    }
}
