package org.arkngbot.commandprocessors;

import org.arkngbot.commandprocessors.impl.TtcCommandProcessor;
import org.arkngbot.services.TTCSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TtcCommandProcessorTest {

    private static final String INCORRECT_USAGE = "Incorrect usage.\nTry using `arkng ttc search <query>` or `arkng ttc price <query>`.";
    private static final String SEARCH_QUERY = "search+query";
    private static final String SEARCH_QUERY_WORD_1 = "search";
    private static final String SEARCH_QUERY_WORD_2 = "query";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not process your query :frowning:";
    private static final String TTC = "ttc";
    private static final String TTC_SEARCH_RESULT = "ttcSearchResult";
    private static final String TTC_PRICE_CHECK_RESULT = "ttcPriceCheckResult";
    private static final String TTC_SEARCH = "search";
    private static final String TTC_PRICE = "price";

    private CommandProcessor commandProcessor;

    private TTCSearchService ttcSearchService;

    @BeforeEach
    public void setUp() {
        ttcSearchService = mock(TTCSearchService.class);
        commandProcessor = new TtcCommandProcessor(ttcSearchService);
    }

    @Test
    public void shouldReturnTtcSearchResult() {
        when(ttcSearchService.search(SEARCH_QUERY)).thenReturn(TTC_SEARCH_RESULT);

        String result = commandProcessor.processCommand(Arrays.asList(TTC_SEARCH, SEARCH_QUERY_WORD_1, SEARCH_QUERY_WORD_2));

        assertThat(result, is(TTC_SEARCH_RESULT));
    }

    @Test
    public void shouldReturnTtcPriceCheckResult() throws Exception {
        when(ttcSearchService.checkPrice(SEARCH_QUERY)).thenReturn(TTC_PRICE_CHECK_RESULT);

        String result = commandProcessor.processCommand(Arrays.asList(TTC_PRICE, SEARCH_QUERY_WORD_1, SEARCH_QUERY_WORD_2));

        assertThat(result, is(TTC_PRICE_CHECK_RESULT));
    }

    @Test
    public void shouldNotReturnResultTooFewArguments() {
        String result = commandProcessor.processCommand(Collections.singletonList(TTC_SEARCH));

        assertThat(result, is(INCORRECT_USAGE));
    }

    @Test
    public void shouldNotReturnResultUnrecognizedFirstArg() {
        String result = commandProcessor.processCommand(Arrays.asList(TTC, SEARCH_QUERY_WORD_2));

        assertThat(result, is(INCORRECT_USAGE));
    }

    @Test
    public void shouldNotProcessCommandExceptionCaughtOnSearch() {
        when(ttcSearchService.search(SEARCH_QUERY)).thenThrow(new RuntimeException());

        String result = commandProcessor.processCommand(Arrays.asList(TTC_SEARCH, SEARCH_QUERY_WORD_1, SEARCH_QUERY_WORD_2));

        assertThat(result, is(ERROR_MESSAGE));
    }

    @Test
    public void shouldNotProcessCommandExceptionCaughtOnPriceCheck() throws Exception {
        when(ttcSearchService.checkPrice(SEARCH_QUERY)).thenThrow(new IOException());

        String result = commandProcessor.processCommand(Arrays.asList(TTC_PRICE, SEARCH_QUERY_WORD_1, SEARCH_QUERY_WORD_2));

        assertThat(result, is(ERROR_MESSAGE));
    }

    @Test
    public void shouldSupportCommand() {
        boolean supports = commandProcessor.supports(TTC);

        assertThat(supports, is(true));
    }

    @Test
    public void shouldNotSupportCommand() {
        boolean supports = commandProcessor.supports(SEARCH_QUERY);

        assertThat(supports, is(false));
    }
}
