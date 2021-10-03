package org.arkngbot.commandprocessors;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.arkngbot.commandprocessors.impl.TtcCommandProcessor;
import org.arkngbot.services.TTCSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TtcCommandProcessorTest {

    private static final String INCORRECT_USAGE = "Incorrect usage.\nTry using `arkng ttc search <query>` or `arkng ttc price <query>`.";
    private static final String SEARCH_QUERY_CONVERTED = "search+query";
    private static final String SEARCH_QUERY = "search query";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not process your query :frowning:";
    private static final String TTC = "ttc";
    private static final String TTC_SEARCH_RESULT = "ttcSearchResult";
    private static final String TTC_PRICE_CHECK_RESULT = "ttcPriceCheckResult";
    private static final String TTC_SEARCH = "search";
    private static final String TTC_PRICE = "price";
    private static final String QUERY_OPTION = "query";
    private static final String SEARCH_DESCRIPTION = "Search TTC for an item";
    private static final String PRICE_DESCRIPTION = "Perform a price check for an item with TTC";
    private static final String TTC_DESCRIPTION = "Retrieve data with TTC";
    private static final String QUERY_OPTION_DESCRIPTION = "The query to search with";
    private static final String INVALID_QUERY_PARAM = "Could not find the query parameter";

    private CommandProcessor commandProcessor;

    private TTCSearchService ttcSearchService;

    @BeforeEach
    public void setUp() {
        ttcSearchService = mock(TTCSearchService.class);
        commandProcessor = new TtcCommandProcessor(ttcSearchService);
    }

    @Test
    public void shouldReturnTtcSearchResult() {
        when(ttcSearchService.search(SEARCH_QUERY_CONVERTED)).thenReturn(TTC_SEARCH_RESULT);

        String result = commandProcessor.processCommand(buildCommand(TTC_SEARCH, SEARCH_QUERY));

        assertThat(result, is(TTC_SEARCH_RESULT));
    }

    @Test
    public void shouldReturnTtcPriceCheckResult() throws Exception {
        when(ttcSearchService.checkPrice(SEARCH_QUERY_CONVERTED)).thenReturn(TTC_PRICE_CHECK_RESULT);

        String result = commandProcessor.processCommand(buildCommand(TTC_PRICE, SEARCH_QUERY));

        assertThat(result, is(TTC_PRICE_CHECK_RESULT));
    }

    @Test
    public void shouldNotReturnResultTooFewArguments() {
        String result = commandProcessor.processCommand(buildCommand(TTC_SEARCH, null));

        assertThat(result, is(INCORRECT_USAGE));
    }

    @Test
    public void shouldNotReturnResultUnrecognizedFirstArg() {
        String result = commandProcessor.processCommand(buildCommand(TTC, "query"));

        assertThat(result, is(INCORRECT_USAGE));
    }

    @Test
    public void shouldNotProcessCommandExceptionCaughtOnSearch() {
        when(ttcSearchService.search(SEARCH_QUERY_CONVERTED)).thenThrow(new RuntimeException());

        String result = commandProcessor.processCommand(buildCommand(TTC_SEARCH, SEARCH_QUERY));

        assertThat(result, is(ERROR_MESSAGE));
    }

    @Test
    public void shouldNotProcessCommandExceptionCaughtOnPriceCheck() {
        when(ttcSearchService.checkPrice(SEARCH_QUERY_CONVERTED)).thenThrow(new RuntimeException());

        String result = commandProcessor.processCommand(buildCommand(TTC_PRICE, SEARCH_QUERY));

        assertThat(result, is(ERROR_MESSAGE));
    }

    @Test
    public void shouldSupportCommand() {
        boolean supports = commandProcessor.supports(TTC);

        assertThat(supports, is(true));
    }

    @Test
    public void shouldNotSupportCommand() {
        boolean supports = commandProcessor.supports(SEARCH_QUERY_CONVERTED);

        assertThat(supports, is(false));
    }

    @Test
    public void shouldBuildRequest() {
        ApplicationCommandOptionData request = commandProcessor.buildRequest();

        assertThat(request.name(), is(TTC));
        assertThat(request.description(), is(TTC_DESCRIPTION));
        assertThat(request.type(), is(ApplicationCommandOption.Type.SUB_COMMAND_GROUP.getValue()));
        assertThat(request.options().get(), hasSize(2));

        ApplicationCommandOptionData searchOption = request.options().get().get(0);
        assertThat(searchOption.name(), is(TTC_SEARCH));
        assertThat(searchOption.description(), is(SEARCH_DESCRIPTION));
        assertThat(searchOption.type(), is(ApplicationCommandOption.Type.SUB_COMMAND.getValue()));
        assertThat(searchOption.required().isAbsent(), is(true));
        verifyQueryOption(searchOption.options().get().get(0));

        ApplicationCommandOptionData priceOption = request.options().get().get(1);
        assertThat(priceOption.name(), is(TTC_PRICE));
        assertThat(priceOption.description(), is(PRICE_DESCRIPTION));
        assertThat(priceOption.type(), is(ApplicationCommandOption.Type.SUB_COMMAND.getValue()));
        assertThat(priceOption.required().isAbsent(), is(true));
        verifyQueryOption(priceOption.options().get().get(0));
    }

    private void verifyQueryOption(ApplicationCommandOptionData queryOption) {
        assertThat(queryOption.name(), is(QUERY_OPTION));
        assertThat(queryOption.description(), is(QUERY_OPTION_DESCRIPTION));
        assertThat(queryOption.type(), is(ApplicationCommandOption.Type.STRING.getValue()));
        assertThat(queryOption.required().get(), is(true));
    }

    private ApplicationCommandInteractionOption buildCommand(String ttcSubcommandName, String query) {
        ApplicationCommandInteractionOption command = mock(ApplicationCommandInteractionOption.class);
        ApplicationCommandInteractionOption ttcSubcommand = mock(ApplicationCommandInteractionOption.class);

        ApplicationCommandInteractionOption queryOption = buildOption(query);

        when(ttcSubcommand.getName()).thenReturn(ttcSubcommandName);
        when(ttcSubcommand.getOption(QUERY_OPTION)).thenReturn(Optional.ofNullable(queryOption));
        if (queryOption != null) {
            when(ttcSubcommand.getOptions()).thenReturn(Collections.singletonList(queryOption));
        }
        when(command.getOptions()).thenReturn(Collections.singletonList(ttcSubcommand));

        return command;
    }

    private ApplicationCommandInteractionOption buildOption(String value) {
        if (value != null) {
            ApplicationCommandInteractionOption option = mock(ApplicationCommandInteractionOption.class);
            ApplicationCommandInteractionOptionValue optionValue = mock(ApplicationCommandInteractionOptionValue.class);
            when(optionValue.asString()).thenReturn(value);
            when(option.getValue()).thenReturn(Optional.of(optionValue));

            return option;
        }
        return null;
    }
}
