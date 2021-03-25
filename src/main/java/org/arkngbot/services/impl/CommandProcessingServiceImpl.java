package org.arkngbot.services.impl;

import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.CommandProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CommandProcessingServiceImpl implements CommandProcessingService {

    private static final String UNKNOWN_COMMAND = "I don't know such a command :frowning:\nType `/arkng help` for a list of all available commands.";
    private static final String SPACE = " ";

    private final List<CommandProcessor> commandProcessors;

    @Autowired
    public CommandProcessingServiceImpl(List<CommandProcessor> commandProcessors) {
        this.commandProcessors = commandProcessors;
    }

    @Override
    public String processCommand(String command) {
        command = command.trim();
        String[] chunks = command.split(SPACE);
        String mainCommand = chunks[1];
        List<String> args = Arrays.asList(Arrays.copyOfRange(chunks, 2, chunks.length));
        return retrieveProcessor(mainCommand)
                .map(proc -> proc.processCommand(args))
                .orElse(UNKNOWN_COMMAND);
    }

    private Optional<CommandProcessor> retrieveProcessor(String mainCommand) {
        return commandProcessors.stream()
                .filter(proc -> proc.supports(mainCommand))
                .findFirst();
    }
}
