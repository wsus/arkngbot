package org.arkngbot.eventprocessors;

import discord4j.core.GatewayDiscordClient;

/**
 * Common interface for all Discord4J event processors.
 */
public interface EventProcessor {

    void processEvent(GatewayDiscordClient client);
}
