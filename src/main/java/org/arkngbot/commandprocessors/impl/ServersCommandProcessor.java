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
public class ServersCommandProcessor implements CommandProcessor {

    private static final String SERVERS_COMMAND = "servers";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the server status :frowning:";
    private static final Logger LOGGER = LogManager.getLogger(ServersCommandProcessor.class);
    private static final String SERVERS_COMMAND_DESCRIPTION = "Check the status of the megaservers";

    private final ESOHubService esoHubService;

    @Autowired
    public ServersCommandProcessor(ESOHubService esoHubService) {
        this.esoHubService = esoHubService;
    }

    @NonNull
    @Override
    public String processCommand(@NonNull ApplicationCommandInteractionOption command) {
        try {
            return esoHubService.checkServers();
        }
        catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            return ERROR_MESSAGE;
        }
    }

    @Override
    public boolean supports(String command) {
        return SERVERS_COMMAND.equals(command);
    }

    @NonNull
    @Override
    public ApplicationCommandOptionData buildRequest() {
        return ApplicationCommandOptionData.builder()
                .name(SERVERS_COMMAND)
                .type(ApplicationCommandOptionType.SUB_COMMAND.getValue())
                .description(SERVERS_COMMAND_DESCRIPTION)
                .build();
    }


}
