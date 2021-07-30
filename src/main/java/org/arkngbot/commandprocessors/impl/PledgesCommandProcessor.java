package org.arkngbot.commandprocessors.impl;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.rest.util.ApplicationCommandOptionType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.ESOHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class PledgesCommandProcessor implements CommandProcessor {

    private static final String PLEDGES_COMMAND = "pledges";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the pledges :frowning:";
    private static final Logger LOGGER = LogManager.getLogger(PledgesCommandProcessor.class);
    private static final String PLEDGES_COMMAND_DESCRIPTION = "See today's Undaunted pledges";

    private final ESOHubService esoHubService;

    @Autowired
    public PledgesCommandProcessor(ESOHubService esoHubService) {
        this.esoHubService = esoHubService;
    }

    @NonNull
    @Override
    public String processCommand(@NonNull ApplicationCommandInteractionOption command) {
        try {
            return esoHubService.checkPledges();
        }
        catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            return ERROR_MESSAGE;
        }
    }

    @Override
    public boolean supports(String command) {
        return PLEDGES_COMMAND.equals(command);
    }

    @NonNull
    @Override
    public ApplicationCommandOptionData buildRequest() {
        return ApplicationCommandOptionData.builder()
                .name(PLEDGES_COMMAND)
                .type(ApplicationCommandOptionType.SUB_COMMAND.getValue())
                .description(PLEDGES_COMMAND_DESCRIPTION)
                .build();
    }


}
