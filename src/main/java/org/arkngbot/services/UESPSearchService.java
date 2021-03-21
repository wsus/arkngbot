package org.arkngbot.services;

import org.arkngbot.datastructures.UESPSearchResult;

/**
 * A service for searching the UESP wiki.
 */
public interface UESPSearchService {

    /**
     * Searches the UESP wiki and returns top five result unless there's a direct hit.
     * @param query the search query
     * @return the search result containing a list of result URLs and a flag indicating if there was a direct hit
     * @throws Exception in case any exception was thrown
     */
    UESPSearchResult searchUESP(String query) throws Exception;
}
