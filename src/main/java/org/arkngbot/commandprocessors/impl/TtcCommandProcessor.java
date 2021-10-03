package org.arkngbot.commandprocessors.impl;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.TTCSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TtcCommandProcessor implements CommandProcessor {

    private static final String INCORRECT_USAGE = "Incorrect usage.\nTry using `arkng ttc search <query>` or `arkng ttc price <query>`.";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not process your query :frowning:";
    private static final String TTC = "ttc";
    private static final String SEARCH_COMMAND = "search";
    private static final String PRICE_COMMAND = "price";
    private static final String PLUS = "+";
    private static final String SEARCH_DESCRIPTION = "Search TTC for an item";
    private static final String PRICE_DESCRIPTION = "Perform a price check for an item with TTC";
    private static final String TTC_DESCRIPTION = "Retrieve data with TTC";
    private static final String QUERY_OPTION_NAME = "query";
    private static final String SPACE = " ";
    private static final String QUERY_OPTION_DESCRIPTION = "The query to search with";
    private static final String INVALID_QUERY_PARAM = "Could not find the query parameter";

    private final TTCSearchService ttcSearchService;

    @Autowired
    public TtcCommandProcessor(TTCSearchService ttcSearchService) {
        this.ttcSearchService = ttcSearchService;
    }

    @NonNull
    @Override
    public String processCommand(@NonNull ApplicationCommandInteractionOption command) {
        if (command.getOptions().size() != 1 || command.getOptions().stream().findFirst().get().getOptions().size() != 1) {
            return INCORRECT_USAGE;
        }

        ApplicationCommandInteractionOption subCommand = command.getOptions().stream().findFirst().get();

        String query = buildQuery(subCommand);
        if (subCommand.getName().equals(SEARCH_COMMAND)) {
            return processSearch(query);
        }
        else if (subCommand.getName().equals(PRICE_COMMAND)) {
            return processPriceCheck(query);
        }
        else {
            return INCORRECT_USAGE;
        }
    }

    private String buildQuery(ApplicationCommandInteractionOption command) {
        return command.getOption(QUERY_OPTION_NAME)
                .map(ApplicationCommandInteractionOption::getValue)
                .flatMap(v -> v.map(ApplicationCommandInteractionOptionValue::asString))
                .map(s -> s.replace(SPACE, PLUS))
                .orElseThrow(() -> new IllegalArgumentException(INVALID_QUERY_PARAM));
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

    @NonNull
    @Override
    public ApplicationCommandOptionData buildRequest() {
        return ApplicationCommandOptionData.builder()
                .name(TTC)
                .description(TTC_DESCRIPTION)
                .type(ApplicationCommandOption.Type.SUB_COMMAND_GROUP.getValue())
                .options(buildOptions())
                .build();
    }

    private List<ApplicationCommandOptionData> buildOptions() {
        ApplicationCommandOptionData search = ApplicationCommandOptionData.builder()
                .name(SEARCH_COMMAND)
                .description(SEARCH_DESCRIPTION)
                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                .addOption(buildQueryOption())
                .build();

        ApplicationCommandOptionData price = ApplicationCommandOptionData.builder()
                .name(PRICE_COMMAND)
                .description(PRICE_DESCRIPTION)
                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                .addOption(buildQueryOption())
                .build();

        return Arrays.asList(search, price);
    }

    private ApplicationCommandOptionData buildQueryOption() {
        return ApplicationCommandOptionData.builder()
                .name(QUERY_OPTION_NAME)
                .description(QUERY_OPTION_DESCRIPTION)
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .required(true)
                .build();
    }
}
