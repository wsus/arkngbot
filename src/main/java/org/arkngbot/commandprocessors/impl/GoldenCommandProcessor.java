package org.arkngbot.commandprocessors.impl;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.ESOHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class GoldenCommandProcessor implements CommandProcessor {

    private static final String GOLDEN_COMMAND = "golden";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the golden items :frowning:";
    private static final Logger LOGGER = LogManager.getLogger(GoldenCommandProcessor.class);
    private static final String GOLDEN_COMMAND_DESCRIPTION = "Check out Adhazabi Aba-daro's golden items";

    private final ESOHubService esoHubService;

    @Autowired
    public GoldenCommandProcessor(ESOHubService esoHubService) {
        this.esoHubService = esoHubService;
    }

    @NonNull
    @Override
    public String processCommand(@NonNull ApplicationCommandInteractionOption command) {
        try {
            return esoHubService.checkGoldenItems();
        }
        catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            return ERROR_MESSAGE;
        }
    }

    @Override
    public boolean supports(String command) {
        return GOLDEN_COMMAND.equals(command);
    }

    @NonNull
    @Override
    public ApplicationCommandOptionData buildRequest() {
        return ApplicationCommandOptionData.builder()
                .name(GOLDEN_COMMAND)
                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                .description(GOLDEN_COMMAND_DESCRIPTION)
                .build();
    }


}
