package org.arkngbot.processors.impl;

import org.arkngbot.processors.CommandProcessor;
import org.arkngbot.services.TTCSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TtcCommandProcessor implements CommandProcessor {

    private static final String INCORRECT_USAGE = "Incorrect usage.\nTry using `arkng ttc search <query>` or `arkng ttc price <query>`.";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not process your query :frowning:";
    private static final String TTC = "ttc";
    private static final String SEARCH_COMMAND = "search";
    private static final String PRICE_COMMAND = "price";
    private static final String PLUS = "+";

    private final TTCSearchService ttcSearchService;

    @Autowired
    public TtcCommandProcessor(TTCSearchService ttcSearchService) {
        this.ttcSearchService = ttcSearchService;
    }

    @Override
    public String processCommand(List<String> args) {
        if (args == null || args.size() < 2) {
            return INCORRECT_USAGE;
        }

        String query = buildQuery(args);
        if (args.get(0).equals(SEARCH_COMMAND)) {
            return processSearch(query);
        }
        else if (args.get(0).equals(PRICE_COMMAND)) {
            return processPriceCheck(query);
        }
        else {
            return INCORRECT_USAGE;
        }
    }

    private String buildQuery(List<String> args) {
        return args.stream()
                .skip(1)
                .collect(Collectors.joining(PLUS));
    }

    private String processSearch(String query){
        try {
            return ttcSearchService.search(query);
        }
        catch (Exception e) {
            return ERROR_MESSAGE;
        }
    }

    private String processPriceCheck(String query){
        try {
            return ttcSearchService.checkPrice(query);
        }
        catch (Exception e) {
            return ERROR_MESSAGE;
        }
    }

    @Override
    public boolean supports(String command) {
        return TTC.equals(command);
    }
}
