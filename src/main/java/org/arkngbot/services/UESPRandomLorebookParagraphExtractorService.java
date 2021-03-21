package org.arkngbot.services;

/**
 * A service for retrieving random lorebook paragraphs from the UESP
 */
public interface UESPRandomLorebookParagraphExtractorService {

    /**
     * Retrieves a random lorebook paragraph from the UESP.
     *
     * @return the paragraph text
     * @throws Exception in case anything goes wrong eg. with URL retrieval
     */
    String extractRandomLorebookParagraph() throws Exception;
}
