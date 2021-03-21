package org.arkngbot.datastructures;

import java.util.List;

/**
 * A structure containing UESP search results.
 */
public class UESPSearchResult {

    /**
     * The result URLs.
     */
    private List<String> searchResultUrls;

    /**
     * If true, there was a direct hit and only one result URL, leading directly to the target page, will be returned.
     */
    private boolean directHit;

    public List<String> getSearchResultUrls() {
        return searchResultUrls;
    }

    public void setSearchResultUrls(List<String> searchResultUrls) {
        this.searchResultUrls = searchResultUrls;
    }

    public boolean isDirectHit() {
        return directHit;
    }

    public void setDirectHit(boolean directHit) {
        this.directHit = directHit;
    }
}
