package org.arkngbot.services;

import org.arkngbot.datastructures.TTCAutocompletionResult;
import org.arkngbot.services.impl.TTCSearchServiceImpl;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TTCSearchServiceTest {

    private static final String SEARCH_TYPE_SELL = "Sell";
    private static final String NO_RESULTS_FOUND = "No results have been found.";
    private static final String PRICE_CHECK_MESSAGE = "Price check for item %s:%nMinimum price: %s%nAverage price: %s%nMaximum price: %s%nSuggested price range: %s";
    private static final String GOLD_AMOUNT_CLASS = "gold-amount";
    private static final String NOT_ENOUGH_DATA = "Not enough data";
    private static final String SUGGESTED_PRICE = "40 - 45";
    private static final String QUERY = "ItemNa";
    private static final String REST_URL = "https://eu.tamrieltradecentre.com/api/pc/Trade/GetItemAutoComplete?term=ItemNa";
    private static final String SEARCH_RESULT_PATTERN = "Returning search results for item ItemName:%nhttps://eu.tamrieltradecentre.com/pc/Trade/SearchResult?SearchType=%s&ItemId=%s&ItemNamePattern=%s&ItemCategory1ID=&ItemTraitID=&ItemQualityID=&IsChampionPoint=false&LevelMin=&LevelMax=&MasterVoucherWritMin=&MasterVoucherWritMax=&AmountMin=&AmountMax=&PriceMin=&PriceMax=";
    private static final String PRICE_CHECK_SEARCH_URL = "https://eu.tamrieltradecentre.com/pc/Trade/SearchResult?SearchType=PriceCheck&ItemId=1234&ItemNamePattern=ItemName&ItemCategory1ID=&ItemTraitID=&ItemQualityID=&IsChampionPoint=false&LevelMin=&LevelMax=&MasterVoucherWritMin=&MasterVoucherWritMax=&AmountMin=&AmountMax=&PriceMin=&PriceMax=";
    private static final String FULL_ITEM_NAME = "ItemName";
    private static final int ITEM_ID = 1234;
    private static final String TRADE_LIST_TABLE = "trade-list-table";
    private static final String MINIMUM = "10";
    private static final String AVERAGE = "50";
    private static final String MAXIMUM = "100";
    private static final String SUGGESTED_LOWER = "40";
    private static final String SUGGESTED_UPPER = "45";
    private static final String NO_DATA_FOUND = "No data found.";

    TTCSearchService ttcSearchService;

    JsoupDocumentRetrievalService jsoupDocumentRetrievalService;

    RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        jsoupDocumentRetrievalService = mock(JsoupDocumentRetrievalService.class);
        restTemplate = mock(RestTemplate.class);
        ttcSearchService = new TTCSearchServiceImpl(jsoupDocumentRetrievalService, restTemplate);
    }

    @Test
    public void shouldReturnASearchResult() {
        TTCAutocompletionResult[] autocompletionResults = buildAutocompletionResults();
        when(restTemplate.getForObject(REST_URL, TTCAutocompletionResult[].class)).thenReturn(autocompletionResults);

        String result = ttcSearchService.search(QUERY);

        assertThat(result, is(String.format(SEARCH_RESULT_PATTERN, SEARCH_TYPE_SELL, ITEM_ID, FULL_ITEM_NAME)));
    }

    @Test
    public void shouldNotReturnASearchResult() {
        when(restTemplate.getForObject(REST_URL, TTCAutocompletionResult[].class)).thenReturn(new TTCAutocompletionResult[]{});

        String result = ttcSearchService.search(QUERY);

        assertThat(result, is(NO_RESULTS_FOUND));
    }

    @Test
    public void shouldReturnAPriceCheckResult() throws Exception {
        TTCAutocompletionResult[] autocompletionResults = buildAutocompletionResults();
        when(restTemplate.getForObject(REST_URL, TTCAutocompletionResult[].class)).thenReturn(autocompletionResults);

        Elements goldAmounts = mockGoldAmountsWithSuggestedPrice();
        mockPriceCheckFlow(goldAmounts);

        String result = ttcSearchService.checkPrice(QUERY);

        assertThat(result, is(String.format(PRICE_CHECK_MESSAGE, FULL_ITEM_NAME, MINIMUM, AVERAGE, MAXIMUM, SUGGESTED_PRICE)));
    }

    @Test
    public void shouldReturnAPriceCheckResultWithoutSuggestedPrice() throws Exception {
        TTCAutocompletionResult[] autocompletionResults = buildAutocompletionResults();
        when(restTemplate.getForObject(REST_URL, TTCAutocompletionResult[].class)).thenReturn(autocompletionResults);

        Elements goldAmounts = mockGoldAmountsWithoutSuggestedPrice();
        mockPriceCheckFlow(goldAmounts);

        String result = ttcSearchService.checkPrice(QUERY);

        assertThat(result, is(String.format(PRICE_CHECK_MESSAGE, FULL_ITEM_NAME, MINIMUM, AVERAGE, MAXIMUM, NOT_ENOUGH_DATA)));
    }

    @Test
    public void shouldNotReturnAPriceCheckResultNoAutocompletion() throws Exception {
        when(restTemplate.getForObject(REST_URL, TTCAutocompletionResult[].class)).thenReturn(new TTCAutocompletionResult[]{});

        String result = ttcSearchService.checkPrice(QUERY);

        assertThat(result, is(NO_RESULTS_FOUND));
    }

    @Test
    public void shouldNotReturnAPriceCheckResultNoDataFound() throws Exception {
        TTCAutocompletionResult[] autocompletionResults = buildAutocompletionResults();
        when(restTemplate.getForObject(REST_URL, TTCAutocompletionResult[].class)).thenReturn(autocompletionResults);
        mockPriceCheckFlowNoData();

        String result = ttcSearchService.checkPrice(QUERY);

        assertThat(result, is(NO_DATA_FOUND));
    }

    private void mockPriceCheckFlow(Elements goldAmounts) throws Exception {
        Document priceCheckResultPage = mock(Document.class);
        Element table = mock(Element.class);

        when(jsoupDocumentRetrievalService.retrieve(PRICE_CHECK_SEARCH_URL)).thenReturn(priceCheckResultPage);
        when(priceCheckResultPage.getElementsByClass(TRADE_LIST_TABLE)).thenReturn(new Elements(table));
        when(table.getElementsByClass(GOLD_AMOUNT_CLASS)).thenReturn(goldAmounts);
    }

    private void mockPriceCheckFlowNoData() throws Exception {
        Document priceCheckResultPage = mock(Document.class);

        when(jsoupDocumentRetrievalService.retrieve(PRICE_CHECK_SEARCH_URL)).thenReturn(priceCheckResultPage);
        when(priceCheckResultPage.getElementsByClass(TRADE_LIST_TABLE)).thenReturn(new Elements());
    }

    private TTCAutocompletionResult[] buildAutocompletionResults() {
        TTCAutocompletionResult result1 = new TTCAutocompletionResult();
        TTCAutocompletionResult result2 = new TTCAutocompletionResult();

        result1.setItemId(ITEM_ID);
        result1.setValue(FULL_ITEM_NAME);

        return new TTCAutocompletionResult[] {result1, result2};
    }

    private Elements mockGoldAmountsWithSuggestedPrice() {
        Elements elements = new Elements();
        elements.add(mockGoldElement(MINIMUM));
        elements.add(mockGoldElement(AVERAGE));
        elements.add(mockGoldElement(MAXIMUM));
        elements.add(mockGoldElement(SUGGESTED_LOWER));
        elements.add(mockGoldElement(SUGGESTED_UPPER));

        return elements;
    }

    private Elements mockGoldAmountsWithoutSuggestedPrice() {
        Elements elements = new Elements();
        elements.add(mockGoldElement(MINIMUM));
        elements.add(mockGoldElement(AVERAGE));
        elements.add(mockGoldElement(MAXIMUM));

        return elements;
    }

    private Element mockGoldElement(String amount) {
        Element element = mock(Element.class);
        when(element.text()).thenReturn(amount);

        return element;
    }
}
