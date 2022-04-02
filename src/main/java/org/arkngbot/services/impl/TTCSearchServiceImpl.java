package org.arkngbot.services.impl;

import org.arkngbot.datastructures.TTCAutocompletionResult;
import org.arkngbot.datastructures.TTCPriceCheckResult;
import org.arkngbot.services.TTCSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Objects;

@Service
public class TTCSearchServiceImpl implements TTCSearchService {

    private static final String SEARCH_RESULTS_URL = "https://eu.tamrieltradecentre.com/pc/Trade/SearchResult";
    private static final String REST_AUTOCOMPLETE_URL = "https://eu.tamrieltradecentre.com/api/pc/Trade/GetItemAutoComplete";
    private static final String REST_PRICE_CHECK_URL = "https://eu.tamrieltradecentre.com/api/pc/Trade/PriceCheck";
    private static final String SEARCH_TYPE_SELL = "Sell";
    private static final String SEARCH_TYPE_PRICE_CHECK = "PriceCheck";
    private static final String NO_RESULTS_FOUND = "No results have been found.";
    private static final String RETURNING_SEARCH_RESULTS = "Returning search results for item %s:%n%s";
    private static final String PRICE_CHECK_MESSAGE = "Price check for item %s:%nMinimum price: %,d%nAverage price: %,.2f%nMaximum price: %,d%nSuggested price range: %s";
    private static final String EMPTY_STRING = "";
    private static final String NO_DATA_FOUND = "No data found.";
    private static final String TERM_PARAM = "term";
    private static final String SEARCH_TYPE_PARAM = "SearchType";
    private static final String ITEM_ID_PARAM = "ItemId";
    private static final String ITEM_NAME_PATTERN_PARAM = "ItemNamePattern";
    private static final String ITEM_CATEGORY_1_ID_PARAM = "ItemCategory1ID";
    private static final String ITEM_TRAIT_ID_PARAM = "ItemTraitID";
    private static final String ITEM_QUALITY_ID_PARAM = "ItemQualityID";
    private static final String FALSE = "false";
    private static final String IS_CHAMPION_POINT_PARAM = "IsChampionPoint";
    private static final String LEVEL_MIN_PARAM = "LevelMin";
    private static final String LEVEL_MAX_PARAM = "LevelMax";
    private static final String MASTER_VOUCHER_WRIT_MIN_PARAM = "MasterVoucherWritMin";
    private static final String MASTER_VOUCHER_WRIT_MAX_PARAM = "MasterVoucherWritMax";
    private static final String AMOUNT_MIN_PARAM = "AmountMin";
    private static final String AMOUNT_MAX_PARAM = "AmountMax";
    private static final String PRICE_MIN_PARAM = "PriceMin";
    private static final String PRICE_MAX_PARAM = "PriceMax";
    private static final String NOT_ENOUGH_DATA = "Not enough data";
    private static final String SUGGESTED_PRICE = "%,.2f - %,.2f";
    private static final int MIN_ENTRIES_FOR_SUGGESTED_PRICE = 10;
    private static final double SUGGESTED_PRICE_UPPER_BOUND_FACTOR = 1.2;

    private final RestTemplate restTemplate;

    @Autowired
    public TTCSearchServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @NonNull
    @Override
    public String search(@NonNull String query) {
        TTCAutocompletionResult firstAutocompletionResult = retrieveFirstAutocompletionResult(query);

        if (firstAutocompletionResult == null) {
            return NO_RESULTS_FOUND;
        }

        return String.format(RETURNING_SEARCH_RESULTS,
                firstAutocompletionResult.getValue(),
                buildSearchUrl(firstAutocompletionResult));
    }

    @NonNull
    @Override
    public String checkPrice(@NonNull String query) {
        TTCAutocompletionResult firstAutocompletionResult = retrieveFirstAutocompletionResult(query);

        if (firstAutocompletionResult == null) {
            return NO_RESULTS_FOUND;
        }

        String priceCheckUrl = buildUrlForPriceCheck(firstAutocompletionResult.getItemId());

        TTCPriceCheckResult priceCheckResult = restTemplate.getForObject(priceCheckUrl, TTCPriceCheckResult.class);
        return processPriceCheckResult(priceCheckResult);
    }

    private TTCAutocompletionResult retrieveFirstAutocompletionResult(String query) {
        String autocompleteRestUrl = buildAutocompleteRestUrl(query);

        TTCAutocompletionResult[] results = restTemplate.getForObject(autocompleteRestUrl, TTCAutocompletionResult[].class);

        if (results == null || results.length == 0) {
            return null;
        }
        else {
            return results[0];
        }
    }

    private String buildAutocompleteRestUrl(String query) {
        return UriComponentsBuilder.fromUriString(REST_AUTOCOMPLETE_URL)
                .queryParam(TERM_PARAM, query)
                .toUriString();
    }

    private String buildSearchUrl(TTCAutocompletionResult autocompletionResult) {
        Integer itemId = autocompletionResult.getItemId();
        String itemName = autocompletionResult.getValue();
        return UriComponentsBuilder.fromUriString(SEARCH_RESULTS_URL)
                .queryParam(SEARCH_TYPE_PARAM, SEARCH_TYPE_SELL)
                .queryParam(ITEM_ID_PARAM, itemId)
                .queryParam(ITEM_NAME_PATTERN_PARAM, itemName)
                .queryParam(ITEM_CATEGORY_1_ID_PARAM, EMPTY_STRING)
                .queryParam(ITEM_TRAIT_ID_PARAM, EMPTY_STRING)
                .queryParam(ITEM_QUALITY_ID_PARAM, EMPTY_STRING)
                .queryParam(IS_CHAMPION_POINT_PARAM, FALSE)
                .queryParam(LEVEL_MIN_PARAM, EMPTY_STRING)
                .queryParam(LEVEL_MAX_PARAM, EMPTY_STRING)
                .queryParam(MASTER_VOUCHER_WRIT_MIN_PARAM, EMPTY_STRING)
                .queryParam(MASTER_VOUCHER_WRIT_MAX_PARAM, EMPTY_STRING)
                .queryParam(AMOUNT_MIN_PARAM, EMPTY_STRING)
                .queryParam(AMOUNT_MAX_PARAM, EMPTY_STRING)
                .queryParam(PRICE_MIN_PARAM, EMPTY_STRING)
                .queryParam(PRICE_MAX_PARAM, EMPTY_STRING)
                .toUriString();
    }

    private String buildUrlForPriceCheck(Integer itemId) {
        return UriComponentsBuilder.fromUriString(REST_PRICE_CHECK_URL)
                .queryParam(SEARCH_TYPE_PARAM, SEARCH_TYPE_PRICE_CHECK)
                .queryParam(ITEM_ID_PARAM, itemId)
                .toUriString();
    }

    private String processPriceCheckResult(TTCPriceCheckResult priceCheckResult) {
        TTCPriceCheckResult.ItemDetailPricePair[] entries = priceCheckResult.getPriceCheckPageModel().getItemDetailPricePairs();

        if (entries == null || Arrays.stream(entries).allMatch(Objects::isNull)) {
            return NO_DATA_FOUND;
        }

        TTCPriceCheckResult.ItemDetailPricePair firstEntry = entries[0];

        return String.format(PRICE_CHECK_MESSAGE,
                firstEntry.getItemDetail().getName(),
                firstEntry.getItemPrice().getPriceMin(),
                firstEntry.getItemPrice().getPriceAvg(),
                firstEntry.getItemPrice().getPriceMax(),
                resolveSuggestedPrice(firstEntry));
    }

    private String resolveSuggestedPrice(TTCPriceCheckResult.ItemDetailPricePair firstEntry) {
        if (firstEntry.getItemPrice().getSuggestedPrice() != null && firstEntry.getItemPrice().getEntryCount() >= MIN_ENTRIES_FOR_SUGGESTED_PRICE) {
            double suggestedLower = firstEntry.getItemPrice().getSuggestedPrice();
            double suggestedUpper = suggestedLower * SUGGESTED_PRICE_UPPER_BOUND_FACTOR;

            return String.format(SUGGESTED_PRICE, suggestedLower, suggestedUpper);
        }
        else {
            return NOT_ENOUGH_DATA;
        }
    }
}
