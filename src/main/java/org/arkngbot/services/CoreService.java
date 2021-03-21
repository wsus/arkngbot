package org.arkngbot.services;

/**
 * The core service of ArkngBot. It is the first service called upon launch.
 */
public interface CoreService {

    /**
     * Initializes the bot.
     * @param token the token necessary to connect the bot to Discord
     */
    void initBot(String token);
}
