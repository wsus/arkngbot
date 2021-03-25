package org.arkngbot.commandprocessors.impl;

import org.arkngbot.datastructures.UESPSearchResult;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.UESPSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Processes a /wiki command.
 */
@Service
public class WikiCommandProcessor implements CommandProcessor {

    private static final String SPACE = " ";
    private static final String UNIVERSAL_NEWLINE = "%n";
    private static final int MAX_SIZE = 5;
    private static final String TOO_FEW_ARGUMENTS = "This command requires one argument!";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the results :frowning:";
    private static final String NO_RESULTS_FOUND = "Your query did not return any results.";
    private static final String RETURNING_FIRST_SEARCH_RESULTS = "Returning first %d search results:%n";
    private static final String WIKI_COMMAND = "wiki";
    private static final String EMPTY_STRING = "";

    private final UESPSearchService uespSearchService;

    @Autowired
    public WikiCommandProcessor(UESPSearchService uespSearchService) {
        this.uespSearchService = uespSearchService;
    }

    @Override
    public String processCommand(List<String> args) {
        if (args == null || args.isEmpty()) {
            return TOO_FEW_ARGUMENTS;
        }
        String query = String.join(SPACE, args);

        try {
            UESPSearchResult searchResult = uespSearchService.searchUESP(query);
            return processResults(searchResult);
        }
        catch (Exception e) {
            return ERROR_MESSAGE;
        }
    }

    @Override
    public boolean supports(String command) {
        return WIKI_COMMAND.equals(command);
    }

    private String processResults(UESPSearchResult searchResult)  {
        List<String> searchResults = searchResult.getSearchResultUrls();

        if (!searchResults.isEmpty()) {
            long count = searchResults.stream().limit(MAX_SIZE).count();
            String result = prepareInitialMessage(searchResult) +
                    searchResults.stream()
                            .limit(MAX_SIZE)
                            .collect(Collectors.joining(UNIVERSAL_NEWLINE));
            return String.format(result, count);
        }
        else {
            return NO_RESULTS_FOUND;
        }
    }

    private String prepareInitialMessage(UESPSearchResult searchResult) {
        if (searchResult.isDirectHit()) {
            return EMPTY_STRING;
        }
        else {
            return RETURNING_FIRST_SEARCH_RESULTS;
        }
    }
}
