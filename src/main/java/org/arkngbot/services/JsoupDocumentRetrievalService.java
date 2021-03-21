package org.arkngbot.services;

import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Wrapper interface to for HTML document retrieval to cut dependency on actual connection.
 */
public interface JsoupDocumentRetrievalService {

    /**
     * Retrieves the HTML document from the given URL.
     *
     * @param url the URL to retrieve the document from
     * @return the retrieved document
     * @throws IOException if something goes wrong
     */
    Document retrieve(String url) throws IOException;
}
