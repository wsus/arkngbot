package org.arkngbot.services;

/**
 * An interface for processing chat commands.
 */
public interface CommandProcessingService {

    /**
     * Processes a command from the chat.
     * @param command the full text of the command starting with "/arkng "
     * @return the response message
     */
    String processCommand(String command);
}
