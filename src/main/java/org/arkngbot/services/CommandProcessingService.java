package org.arkngbot.services;

import discord4j.core.object.command.ApplicationCommandInteraction;

/**
 * An interface for processing chat commands.
 */
public interface CommandProcessingService {

    /**
     * Processes a slash command.
     * @param acid the Discord4J object containing the data of the invoked command
     * @return the response message
     */
    String processCommand(ApplicationCommandInteraction acid);
}
