package org.arkngbot.services.impl;

import org.arkngbot.datastructures.TTCAutocompletionResult;
import org.arkngbot.services.JsoupDocumentRetrievalService;
import org.arkngbot.services.TTCSearchService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
public class TTCSearchServiceImpl implements TTCSearchService {

    private static final String SEARCH_RESULTS_URL = "https://eu.tamrieltradecentre.com/pc/Trade/SearchResult";
    private static final String REST_AUTOCOMPLETE_URL = "https://eu.tamrieltradecentre.com/api/pc/Trade/GetItemAutoComplete";
    private static final String SEARCH_TYPE_SELL = "Sell";
    private static final String SEARCH_TYPE_PRICE_CHECK = "PriceCheck";
    private static final String NO_RESULTS_FOUND = "No results have been found.";
    private static final String RETURNING_SEARCH_RESULTS = "Returning search results for item %s:%n%s";
    private static final String PRICE_CHECK_MESSAGE = "Price check for item %s:%nMinimum price: %s%nAverage price: %s%nMaximum price: %s%nSuggested price range: %s";
    private static final String EMPTY_STRING = "";
    private static final String GOLD_AMOUNT_CLASS = "gold-amount";
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
    private static final String SUGGESTED_PRICE = "%s - %s";
    private static final String TRADE_LIST_TABLE = "trade-list-table";

    private final JsoupDocumentRetrievalService jsoupDocumentRetrievalService;

    private final RestTemplate restTemplate;

    @Autowired
    public TTCSearchServiceImpl(JsoupDocumentRetrievalService jsoupDocumentRetrievalService, RestTemplate restTemplate) {
        this.jsoupDocumentRetrievalService = jsoupDocumentRetrievalService;
        this.restTemplate = restTemplate;
    }

    @Override
    public String search(String query) {
        TTCAutocompletionResult firstAutocompletionResult = retrieveFirstAutocompletionResult(query);

        if (firstAutocompletionResult == null) {
            return NO_RESULTS_FOUND;
        }

        return String.format(RETURNING_SEARCH_RESULTS,
                firstAutocompletionResult.getValue(),
                buildSearchUrl(firstAutocompletionResult, SEARCH_TYPE_SELL));
    }

    @Override
    public String checkPrice(String query) throws IOException {
        TTCAutocompletionResult firstAutocompletionResult = retrieveFirstAutocompletionResult(query);

        if (firstAutocompletionResult == null) {
            return NO_RESULTS_FOUND;
        }

        String searchUrl = buildSearchUrl(firstAutocompletionResult, SEARCH_TYPE_PRICE_CHECK);

        Document priceCheckResult = jsoupDocumentRetrievalService.retrieve(searchUrl);
        return processPriceCheckResult(priceCheckResult, firstAutocompletionResult.getValue());
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

    private String buildSearchUrl(TTCAutocompletionResult autocompletionResult, String searchType) {
        Integer itemId = autocompletionResult.getItemId();
        String itemName = autocompletionResult.getValue();
        return UriComponentsBuilder.fromUriString(SEARCH_RESULTS_URL)
                .queryParam(SEARCH_TYPE_PARAM, searchType)
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

    private String processPriceCheckResult(Document priceCheckResult, String itemName) {
        Element table = priceCheckResult.getElementsByClass(TRADE_LIST_TABLE).stream()
                .findFirst()
                .orElse(null);

        if (table == null) {
            return NO_DATA_FOUND;
        }

        Elements goldElements = table.getElementsByClass(GOLD_AMOUNT_CLASS);

        return buildPriceCheckMessage(goldElements, itemName);
    }

    String buildPriceCheckMessage(Elements goldElements, String itemName) {
        String minimum = goldElements.get(0).text();
        String average = goldElements.get(1).text();
        String maximum = goldElements.get(2).text();
        String suggestedPriceRange = resolveSuggestedPrice(goldElements);

        return String.format(PRICE_CHECK_MESSAGE,
                itemName, minimum, average, maximum, suggestedPriceRange);
    }

    private String resolveSuggestedPrice(Elements goldElements) {
        if (goldElements.size() >= 5) {
            String suggestedLower = goldElements.get(3).text();
            String suggestedUpper = goldElements.get(4).text();

            return String.format(SUGGESTED_PRICE, suggestedLower, suggestedUpper);
        }
        else {
            return NOT_ENOUGH_DATA;
        }
    }
}
