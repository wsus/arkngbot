package org.arkngbot.commandprocessors;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.springframework.lang.NonNull;

/**
 * Common interface for all command processors.
 */
public interface CommandProcessor {

    /**
     * Processes a command specific to the processor implementation.
     * @param command the command data
     * @return the message that will be writted to the chat in reaction
     */
    @NonNull
    String processCommand(@NonNull ApplicationCommandInteractionOption command);

    /**
     * Checks if the processor supports a given command word.
     * @param command the command word
     * @return true if supported
     */
    boolean supports(@NonNull String command);

    /**
     * Builds a request for Discord4J's REST client.
     * @return the assembled request
     */
    @NonNull
    ApplicationCommandOptionData buildRequest();
}
