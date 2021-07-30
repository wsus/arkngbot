package org.arkngbot.services.impl;

import discord4j.core.object.command.ApplicationCommandInteraction;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.CommandProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommandProcessingServiceImpl implements CommandProcessingService {

    private static final String UNKNOWN_COMMAND = "I don't know such a command :frowning:\nType `/arkng help` for a list of all available commands.";
    private static final String NO_SUBCOMMAND = "No concrete subcommand could be determined";

    private final List<CommandProcessor> commandProcessors;

    @Autowired
    public CommandProcessingServiceImpl(List<CommandProcessor> commandProcessors) {
        this.commandProcessors = commandProcessors;
    }

    @Override
    public String processCommand(ApplicationCommandInteraction acid) {
        ApplicationCommandInteractionOption firstOption = retrieveFirstOption(acid);
        return retrieveProcessor(firstOption.getName())
                .map(proc -> proc.processCommand(firstOption))
                .orElse(UNKNOWN_COMMAND);
    }

    private Optional<CommandProcessor> retrieveProcessor(String mainCommandName) {
        return commandProcessors.stream()
                .filter(proc -> proc.supports(mainCommandName))
                .findFirst();
    }

    private ApplicationCommandInteractionOption retrieveFirstOption(ApplicationCommandInteraction acid) {
        return acid.getOptions().stream().findFirst().orElseThrow(() -> new IllegalArgumentException(NO_SUBCOMMAND));
    }
}
