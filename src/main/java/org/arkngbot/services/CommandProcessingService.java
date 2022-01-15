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

    /**
     * For a given slash command, checks if the reply should be private (in DM)
     *
     * @param acid the Discord4J object containing the data of the invoked command
     * @return true if the reply should be private (in DM), false otherwise
     */
    boolean checkPrivateReply(ApplicationCommandInteraction acid);
}
