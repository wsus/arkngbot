package org.arkngbot.commandprocessors;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.rest.util.ApplicationCommandOptionType;
import org.arkngbot.commandprocessors.impl.WikiCommandProcessor;
import org.arkngbot.datastructures.UESPSearchResult;
import org.arkngbot.services.UESPSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WikiCommandProcessorTest {

    private static final String FIVE_RESULTS_PATTERN = "Returning first 5 search results:%n/searchResultUrl1%n/searchResultUrl2%n/searchResultUrl3%n/searchResultUrl4%n/searchResultUrl5";
    private static final String TWO_RESULTS_PATTERN = "Returning first 2 search results:%n/searchResultUrl1%n/searchResultUrl2";
    private static final String SEARCH_QUERY = "search query";
    private static final String NO_RESULTS_FOUND = "Your query did not return any results.";
    private static final String DIRECT_URL = "/directUrl";
    private static final String SEARCH_RESULT_URL = "/searchResultUrl";
    private static final String TOO_FEW_ARGUMENTS = "This command requires one argument!";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the results :frowning:";
    private static final String WIKI = "wiki";
    private static final String QUERY_OPTION = "query";
    private static final String QUERY_OPTION_DESCRIPTION = "The query to search with";
    private static final String WIKI_COMMAND_DESCRIPTION = "Search the UESP Wiki";

    private CommandProcessor commandProcessor;

    private UESPSearchService uespSearchService;

    @BeforeEach
    public void setUp() {
        uespSearchService = mock(UESPSearchService.class);
        commandProcessor = new WikiCommandProcessor(uespSearchService);
    }

    @Test
    public void shouldReturnFiveOutOfSevenResults() throws Exception {
        when(uespSearchService.searchUESP(SEARCH_QUERY)).thenReturn(buildResult(7));

        String result = commandProcessor.processCommand(buildCommand(SEARCH_QUERY));

        assertThat(result, is(String.format(FIVE_RESULTS_PATTERN)));
    }

    @Test
    public void shouldReturnTwoResults() throws Exception {
        when(uespSearchService.searchUESP(SEARCH_QUERY)).thenReturn(buildResult(2));

        String result = commandProcessor.processCommand(buildCommand(SEARCH_QUERY));

        assertThat(result, is(String.format(TWO_RESULTS_PATTERN)));
    }

    @Test
    public void shouldReturnDirectResult() throws Exception {
        when(uespSearchService.searchUESP(SEARCH_QUERY)).thenReturn(buildDirectResult());

        String result = commandProcessor.processCommand(buildCommand(SEARCH_QUERY));

        assertThat(result, is(DIRECT_URL));
    }

    @Test
    public void shouldReturnNoResult() throws Exception {
        when(uespSearchService.searchUESP(SEARCH_QUERY)).thenReturn(buildEmptyResult());

        String result = commandProcessor.processCommand(buildCommand(SEARCH_QUERY));

        assertThat(result, is(NO_RESULTS_FOUND));
    }

    @Test
    public void shouldNotProcessCommandTooFewArguments() throws Exception {

        String result = commandProcessor.processCommand(buildCommand(null));

        assertThat(result, is(TOO_FEW_ARGUMENTS));
    }

    @Test
    public void shouldNotProcessCommandExceptionCaught() throws Exception {
        when(uespSearchService.searchUESP(SEARCH_QUERY)).thenThrow(new IOException());

        String result = commandProcessor.processCommand(buildCommand(SEARCH_QUERY));

        assertThat(result, is(ERROR_MESSAGE));
    }

    @Test
    public void shouldSupportCommand() {
        boolean supports = commandProcessor.supports(WIKI);

        assertThat(supports, is(true));
    }

    @Test
    public void shouldNotSupportCommand() {
        boolean supports = commandProcessor.supports(SEARCH_QUERY);

        assertThat(supports, is(false));
    }

    @Test
    public void shouldBuildRequest() {
        ApplicationCommandOptionData request = commandProcessor.buildRequest();

        assertThat(request.name(), is(WIKI));
        assertThat(request.description(), is(WIKI_COMMAND_DESCRIPTION));
        assertThat(request.type(), is(ApplicationCommandOptionType.SUB_COMMAND.getValue()));
        assertThat(request.options().get(), hasSize(1));

        ApplicationCommandOptionData raceOption = request.options().get().get(0);
        assertThat(raceOption.name(), is(QUERY_OPTION));
        assertThat(raceOption.description(), is(QUERY_OPTION_DESCRIPTION));
        assertThat(raceOption.type(), is(ApplicationCommandOptionType.STRING.getValue()));
        assertThat(raceOption.required().get(), is(true));
    }

    private UESPSearchResult buildResult(int numberOfResults) {
        UESPSearchResult result = new UESPSearchResult();
        result.setSearchResultUrls(new ArrayList<>());

        for (int i = 1; i <= numberOfResults; i++) {
            result.getSearchResultUrls().add(SEARCH_RESULT_URL + i);
        }

        return result;
    }

    private UESPSearchResult buildDirectResult() {
        UESPSearchResult result = new UESPSearchResult();
        result.setSearchResultUrls(new ArrayList<>());
        result.getSearchResultUrls().add(DIRECT_URL);
        result.setDirectHit(true);

        return result;
    }

    private UESPSearchResult buildEmptyResult() {
        UESPSearchResult result = new UESPSearchResult();
        result.setSearchResultUrls(new ArrayList<>());

        return result;
    }

    private ApplicationCommandInteractionOption buildCommand(String query) {
        ApplicationCommandInteractionOption command = mock(ApplicationCommandInteractionOption.class);

        ApplicationCommandInteractionOption raceOption = buildOption(query);

        when(command.getOption(QUERY_OPTION)).thenReturn(Optional.ofNullable(raceOption));

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
