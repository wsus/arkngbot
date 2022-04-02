package org.arkngbot.services;

import org.arkngbot.datastructures.TTCAutocompletionResult;
import org.arkngbot.datastructures.TTCPriceCheckResult;
import org.arkngbot.services.impl.TTCSearchServiceImpl;
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
    private static final String PRICE_CHECK_MESSAGE = "Price check for item %s:%nMinimum price: %,d%nAverage price: %,.2f%nMaximum price: %,d%nSuggested price range: %s";
    private static final String NOT_ENOUGH_DATA = "Not enough data";
    private static final String SUGGESTED_PRICE = "%,.2f - %,.2f";
    private static final String QUERY = "ItemNa";
    private static final String REST_URL = "https://eu.tamrieltradecentre.com/api/pc/Trade/GetItemAutoComplete?term=ItemNa";
    private static final String REST_PRICE_CHECK_URL = "https://eu.tamrieltradecentre.com/api/pc/Trade/PriceCheck?SearchType=PriceCheck&ItemId=1234";
    private static final String SEARCH_RESULT_PATTERN = "Returning search results for item ItemName:%nhttps://eu.tamrieltradecentre.com/pc/Trade/SearchResult?SearchType=%s&ItemId=%s&ItemNamePattern=%s&ItemCategory1ID=&ItemTraitID=&ItemQualityID=&IsChampionPoint=false&LevelMin=&LevelMax=&MasterVoucherWritMin=&MasterVoucherWritMax=&AmountMin=&AmountMax=&PriceMin=&PriceMax=";
    private static final String FULL_ITEM_NAME = "ItemName";
    private static final int ITEM_ID = 1234;
    private static final int MINIMUM = 10;
    private static final double AVERAGE = 50d;
    private static final int MAXIMUM = 100;
    private static final double SUGGESTED_LOWER = 40;
    private static final double SUGGESTED_UPPER = 48;
    private static final String NO_DATA_FOUND = "No data found.";
    private static final Integer ENTRY_COUNT_ENOUGH_FOR_SUGGESTED = 10;
    private static final Integer ENTRY_COUNT_NOT_ENOUGH_FOR_SUGGESTED = 5;

    TTCSearchService ttcSearchService;

    RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        restTemplate = mock(RestTemplate.class);
        ttcSearchService = new TTCSearchServiceImpl(restTemplate);
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
    public void shouldReturnAPriceCheckResult() {
        TTCAutocompletionResult[] autocompletionResults = buildAutocompletionResults();
        when(restTemplate.getForObject(REST_URL, TTCAutocompletionResult[].class)).thenReturn(autocompletionResults);
        TTCPriceCheckResult.ItemDetailPricePair entry = mockEntryWithSuggestedPrice();
        when(restTemplate.getForObject(REST_PRICE_CHECK_URL, TTCPriceCheckResult.class)).thenReturn(buildPriceCheckResult(entry));

        String result = ttcSearchService.checkPrice(QUERY);

        assertThat(result, is(String.format(PRICE_CHECK_MESSAGE, FULL_ITEM_NAME, MINIMUM, AVERAGE, MAXIMUM,
                String.format(SUGGESTED_PRICE, SUGGESTED_LOWER, SUGGESTED_UPPER))));
    }

    @Test
    public void shouldReturnAPriceCheckResultWithoutSuggestedPrice() {
        TTCAutocompletionResult[] autocompletionResults = buildAutocompletionResults();
        when(restTemplate.getForObject(REST_URL, TTCAutocompletionResult[].class)).thenReturn(autocompletionResults);
        TTCPriceCheckResult.ItemDetailPricePair entry = mockEntryWithoutSuggestedPrice();
        when(restTemplate.getForObject(REST_PRICE_CHECK_URL, TTCPriceCheckResult.class)).thenReturn(buildPriceCheckResult(entry));

        String result = ttcSearchService.checkPrice(QUERY);

        assertThat(result, is(String.format(PRICE_CHECK_MESSAGE, FULL_ITEM_NAME, MINIMUM, AVERAGE, MAXIMUM, NOT_ENOUGH_DATA)));
    }

    @Test
    public void shouldReturnAPriceCheckResultWithoutSuggestedPriceTooFewEntries() {
        TTCAutocompletionResult[] autocompletionResults = buildAutocompletionResults();
        when(restTemplate.getForObject(REST_URL, TTCAutocompletionResult[].class)).thenReturn(autocompletionResults);
        TTCPriceCheckResult.ItemDetailPricePair entry = mockEntryWithTooFewEntriesForSuggestedPrice();
        when(restTemplate.getForObject(REST_PRICE_CHECK_URL, TTCPriceCheckResult.class)).thenReturn(buildPriceCheckResult(entry));

        String result = ttcSearchService.checkPrice(QUERY);

        assertThat(result, is(String.format(PRICE_CHECK_MESSAGE, FULL_ITEM_NAME, MINIMUM, AVERAGE, MAXIMUM, NOT_ENOUGH_DATA)));
    }

    @Test
    public void shouldNotReturnAPriceCheckResultNoAutocompletion() {
        when(restTemplate.getForObject(REST_URL, TTCAutocompletionResult[].class)).thenReturn(new TTCAutocompletionResult[]{});

        String result = ttcSearchService.checkPrice(QUERY);

        assertThat(result, is(NO_RESULTS_FOUND));
    }

    @Test
    public void shouldNotReturnAPriceCheckResultNoDataFound() {
        TTCAutocompletionResult[] autocompletionResults = buildAutocompletionResults();
        when(restTemplate.getForObject(REST_URL, TTCAutocompletionResult[].class)).thenReturn(autocompletionResults);
        when(restTemplate.getForObject(REST_PRICE_CHECK_URL, TTCPriceCheckResult.class)).thenReturn(buildPriceCheckResult(null));

        String result = ttcSearchService.checkPrice(QUERY);

        assertThat(result, is(NO_DATA_FOUND));
    }

    private TTCAutocompletionResult[] buildAutocompletionResults() {
        TTCAutocompletionResult result1 = new TTCAutocompletionResult();
        TTCAutocompletionResult result2 = new TTCAutocompletionResult();

        result1.setItemId(ITEM_ID);
        result1.setValue(FULL_ITEM_NAME);

        return new TTCAutocompletionResult[] {result1, result2};
    }

    private TTCPriceCheckResult buildPriceCheckResult(TTCPriceCheckResult.ItemDetailPricePair entry) {
        TTCPriceCheckResult result = new TTCPriceCheckResult();
        TTCPriceCheckResult.PriceCheckPageModel model = new TTCPriceCheckResult.PriceCheckPageModel();
        model.setItemDetailPricePairs(new TTCPriceCheckResult.ItemDetailPricePair[]{entry});
        result.setPriceCheckPageModel(model);

        return result;
    }

    private TTCPriceCheckResult.ItemDetailPricePair mockEntryWithSuggestedPrice() {
        TTCPriceCheckResult.ItemDetailPricePair entry = new TTCPriceCheckResult.ItemDetailPricePair();
        TTCPriceCheckResult.ItemDetail itemDetail = new TTCPriceCheckResult.ItemDetail();
        TTCPriceCheckResult.ItemPrice priceDetail = new TTCPriceCheckResult.ItemPrice();
        itemDetail.setName(FULL_ITEM_NAME);
        priceDetail.setPriceMin(MINIMUM);
        priceDetail.setPriceMax(MAXIMUM);
        priceDetail.setPriceAvg(AVERAGE);
        priceDetail.setSuggestedPrice(SUGGESTED_LOWER);
        priceDetail.setEntryCount(ENTRY_COUNT_ENOUGH_FOR_SUGGESTED);
        entry.setItemDetail(itemDetail);
        entry.setItemPrice(priceDetail);

        return entry;
    }

    private TTCPriceCheckResult.ItemDetailPricePair mockEntryWithoutSuggestedPrice() {
        TTCPriceCheckResult.ItemDetailPricePair entry = new TTCPriceCheckResult.ItemDetailPricePair();
        TTCPriceCheckResult.ItemDetail itemDetail = new TTCPriceCheckResult.ItemDetail();
        TTCPriceCheckResult.ItemPrice priceDetail = new TTCPriceCheckResult.ItemPrice();
        itemDetail.setName(FULL_ITEM_NAME);
        priceDetail.setPriceMin(MINIMUM);
        priceDetail.setPriceMax(MAXIMUM);
        priceDetail.setPriceAvg(AVERAGE);
        priceDetail.setEntryCount(ENTRY_COUNT_ENOUGH_FOR_SUGGESTED);
        entry.setItemDetail(itemDetail);
        entry.setItemPrice(priceDetail);

        return entry;
    }

    private TTCPriceCheckResult.ItemDetailPricePair mockEntryWithTooFewEntriesForSuggestedPrice() {
        TTCPriceCheckResult.ItemDetailPricePair entry = new TTCPriceCheckResult.ItemDetailPricePair();
        TTCPriceCheckResult.ItemDetail itemDetail = new TTCPriceCheckResult.ItemDetail();
        TTCPriceCheckResult.ItemPrice priceDetail = new TTCPriceCheckResult.ItemPrice();
        itemDetail.setName(FULL_ITEM_NAME);
        priceDetail.setPriceMin(MINIMUM);
        priceDetail.setPriceMax(MAXIMUM);
        priceDetail.setPriceAvg(AVERAGE);
        priceDetail.setSuggestedPrice(SUGGESTED_LOWER);
        priceDetail.setEntryCount(ENTRY_COUNT_NOT_ENOUGH_FOR_SUGGESTED);
        entry.setItemDetail(itemDetail);
        entry.setItemPrice(priceDetail);

        return entry;
    }
}
