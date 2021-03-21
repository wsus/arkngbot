package org.arkngbot.services.impl;

import org.arkngbot.datastructures.UESPSearchResult;
import org.arkngbot.services.JsoupDocumentRetrievalService;
import org.arkngbot.services.UESPSearchService;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class UESPSearchServiceImpl implements UESPSearchService {

    private static final String SEARCH_FORM_ID = "searchform";
    private static final String SEARCH_INPUT_ID = "searchInput";
    private static final String UESP_URL = "https://en.uesp.net";
    private static final String ATTRIBUTE_VALUE = "value";
    private static final String ATTRIBUTE_HREF = "href";
    private static final String SEARCH_RESULT_HEADING_CLASS = "mw-search-result-heading";
    private static final String PERCENT = "%";
    private static final String ESCAPED_PERCENT = "%%";
    private static final String FIRST_HEADING = "firstHeading";

    private final JsoupDocumentRetrievalService jsoupDocumentRetrievalService;

    @Autowired
    public UESPSearchServiceImpl(JsoupDocumentRetrievalService jsoupDocumentRetrievalService) {
        this.jsoupDocumentRetrievalService = jsoupDocumentRetrievalService;
    }

    @Override
    public UESPSearchResult searchUESP(String query) throws Exception {

        Document mainPage = jsoupDocumentRetrievalService.retrieve(UESP_URL);

        FormElement searchForm = (FormElement) mainPage.getElementById(SEARCH_FORM_ID);
        Element searchInput = searchForm.getElementById(SEARCH_INPUT_ID);
        searchInput.attr(ATTRIBUTE_VALUE, query);
        Connection connection = searchForm.submit();

        Document searchResultsPage = connection.get();

        return buildSearchResult(searchResultsPage, query);
    }

    private boolean isDirectHit(Document page, String query) {
        Element header = page.getElementById(FIRST_HEADING);
        return header.text().equalsIgnoreCase(query);
    }

    private UESPSearchResult buildSearchResult(Document searchResultsPage, String query) {
        UESPSearchResult result = new UESPSearchResult();

        if (isDirectHit(searchResultsPage, query)) {
            result.setDirectHit(true);
            result.setSearchResultUrls(Collections.singletonList(searchResultsPage.location().replace(PERCENT, ESCAPED_PERCENT)));
        }
        else {
            Elements searchResults = searchResultsPage.getElementsByClass(SEARCH_RESULT_HEADING_CLASS);

            result.setSearchResultUrls(searchResults.stream()
                    .map(sr -> UESP_URL + sr.child(0).attr(ATTRIBUTE_HREF))
                    .map(relativeUrl -> relativeUrl.replace(PERCENT, ESCAPED_PERCENT))
                    .collect(Collectors.toList()));
        }

        return result;
    }
}
