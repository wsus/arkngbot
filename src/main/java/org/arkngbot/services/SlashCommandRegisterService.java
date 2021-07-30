package org.arkngbot.services;

import discord4j.rest.RestClient;
import org.springframework.lang.NonNull;

/**
 * Handles registering slash commands.
 */
public interface SlashCommandRegisterService {

    /**
     * Registers all slash commands for which command processors exist.
     * @param client the Discord REST client
     */
    void registerSlashCommands(@NonNull RestClient client);
}
