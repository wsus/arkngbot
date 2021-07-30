package org.arkngbot.services;

import org.springframework.lang.NonNull;

/**
 * A service for retrieving data from ESOHub.
 */
public interface ESOHubService {

    /**
     * Checks the current dungeon pledges.
     * @return the message containing current pledges
     * @throws Exception if something goes wrong
     */
    @NonNull
    String checkPledges() throws Exception;

    /**
     * Checks the current luxury furnishings offered by Zanil Theran
     * @return the message containing current luxury furnishings if available
     * @throws Exception if something goes wrong
     */
    @NonNull
    String checkLuxuryFurnishings() throws Exception;

    /**
     * Checks the current golden items offered by Adhazabi Aba-daro
     * @return the message containing current golden items if available
     * @throws Exception if something goes wrong
     */
    @NonNull
    String checkGoldenItems() throws Exception;

    /**
     * Checks which servers are currently up
     * @return the message containing current server status
     * @throws Exception if something goes wrong
     */
    @NonNull
    String checkServers() throws Exception;
}
