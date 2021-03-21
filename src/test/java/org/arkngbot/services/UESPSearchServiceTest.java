package org.arkngbot.services;

import org.arkngbot.datastructures.UESPSearchResult;
import org.arkngbot.services.impl.UESPSearchServiceImpl;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class UESPSearchServiceTest {

    private static final String SEARCH_FORM_ID = "searchform";
    private static final String SEARCH_INPUT_ID = "searchInput";
    private static final String UESP_URL = "https://en.uesp.net";
    private static final String ATTRIBUTE_VALUE = "value";
    private static final String ATTRIBUTE_HREF = "href";
    private static final String SEARCH_RESULT_HEADING_CLASS = "mw-search-result-heading";
    private static final String FIRST_HEADING = "firstHeading";
    private static final String FIRST_SEARCH_RESULT_URL = "/searchResult%20Url";
    private static final String FIRST_SEARCH_RESULT_ESCAPED_URL = "/searchResult%%20Url";
    private static final String SECOND_SEARCH_RESULT_URL = "/secondSearchResultUrl";
    private static final String DIRECT_SEARCH_RESULT_URL = UESP_URL + "/directSearchResultUrl";
    private static final String QUERY = "query";
    private static final String INDIRECT_RESULT_HEADER = "notTheSameAsQuery";
    private static final String DIRECT_RESULT_HEADER = "Query";

    UESPSearchService uespSearchService;

    JsoupDocumentRetrievalService jsoupDocumentRetrievalService;

    @BeforeEach
    public void setUp() {
        jsoupDocumentRetrievalService = mock(JsoupDocumentRetrievalService.class);
        uespSearchService = new UESPSearchServiceImpl(jsoupDocumentRetrievalService);
    }

    @Test
    public void shouldReturnTwoIndirectResults() throws Exception {
        Element searchInput = mock(Element.class);
        Document mainPage = mock(Document.class);
        Elements searchResults = mockTwoSearchResults();
        mockFlow(mainPage, searchInput, searchResults, INDIRECT_RESULT_HEADER);
        when(jsoupDocumentRetrievalService.retrieve(UESP_URL)).thenReturn(mainPage);

        UESPSearchResult result = uespSearchService.searchUESP(QUERY);

        assertThat(result.isDirectHit(), is(false));
        assertThat(result.getSearchResultUrls(), hasSize(2));
        assertThat(result.getSearchResultUrls().get(0), is(UESP_URL + FIRST_SEARCH_RESULT_ESCAPED_URL));
        assertThat(result.getSearchResultUrls().get(1), is(UESP_URL + SECOND_SEARCH_RESULT_URL));
        verify(searchInput).attr(ATTRIBUTE_VALUE, QUERY);
    }

    @Test
    public void shouldReturnADirectResult() throws Exception {
        Element searchInput = mock(Element.class);
        Document mainPage = mock(Document.class);
        Elements searchResults = mockTwoSearchResults();
        mockFlow(mainPage, searchInput, searchResults, DIRECT_RESULT_HEADER);
        when(jsoupDocumentRetrievalService.retrieve(UESP_URL)).thenReturn(mainPage);

        UESPSearchResult result = uespSearchService.searchUESP(QUERY);

        assertThat(result.isDirectHit(), is(true));
        assertThat(result.getSearchResultUrls(), hasSize(1));
        assertThat(result.getSearchResultUrls().get(0), is(DIRECT_SEARCH_RESULT_URL));
        verify(searchInput).attr(ATTRIBUTE_VALUE, QUERY);
    }

    @Test
    public void shouldReturnNoResults() throws Exception {
        Element searchInput = mock(Element.class);
        Document mainPage = mock(Document.class);
        mockFlow(mainPage, searchInput, new Elements(), INDIRECT_RESULT_HEADER);
        when(jsoupDocumentRetrievalService.retrieve(UESP_URL)).thenReturn(mainPage);

        UESPSearchResult result = uespSearchService.searchUESP(QUERY);

        assertThat(result.isDirectHit(), is(false));
        assertThat(result.getSearchResultUrls(), hasSize(0));
        verify(searchInput).attr(ATTRIBUTE_VALUE, QUERY);
    }

    private Elements mockTwoSearchResults() {
        Elements searchResults = new Elements();

        Element firstSearchResult = mockIndirectSearchResult(FIRST_SEARCH_RESULT_URL);
        Element secondSearchResult = mockIndirectSearchResult(SECOND_SEARCH_RESULT_URL);
        searchResults.addAll(Arrays.asList(firstSearchResult, secondSearchResult));

        return searchResults;
    }

    private void mockFlow(Document mainPage, Element searchInput, Elements searchResults, String resultPageHeader) throws Exception {
        FormElement searchForm = mock(FormElement.class);
        Connection connection = mock(Connection.class);
        Document searchResultPage = mock(Document.class);
        Element header = mock(Element.class);

        when(mainPage.getElementById(SEARCH_FORM_ID)).thenReturn(searchForm);
        when(searchForm.getElementById(SEARCH_INPUT_ID)).thenReturn(searchInput);
        when(searchForm.submit()).thenReturn(connection);
        when(connection.get()).thenReturn(searchResultPage);
        when(searchResultPage.getElementById(FIRST_HEADING)).thenReturn(header);
        when(header.text()).thenReturn(resultPageHeader);
        when(searchResultPage.getElementsByClass(SEARCH_RESULT_HEADING_CLASS)).thenReturn(searchResults);
        when(searchResultPage.location()).thenReturn(DIRECT_SEARCH_RESULT_URL);
    }

    private Element mockIndirectSearchResult(String urlToReturn) {
        Element searchResult = mock(Element.class);
        Element a = mock(Element.class);
        when(searchResult.child(0)).thenReturn(a);
        when(a.attr(ATTRIBUTE_HREF)).thenReturn(urlToReturn);

        return searchResult;
    }
}
