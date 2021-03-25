package org.arkngbot.commandprocessors;

import java.util.List;

/**
 * Common interface for all command processors.
 */
public interface CommandProcessor {

    /**
     * Processes a command specific to the processor implementation.
     * @param args the command arguments
     * @return the message that will be writted to the chat in reaction
     */
    String processCommand(List<String> args);

    /**
     * Checks if the processor supports a given command word.
     * @param command the command word
     * @return true if supported
     */
    boolean supports(String command);
}
