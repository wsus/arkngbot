package org.arkngbot.services.impl;

import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.SlashCommandRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlashCommandRegisterServiceImpl implements SlashCommandRegisterService {

    private static final Logger LOGGER = LogManager.getLogger(SlashCommandRegisterServiceImpl.class);
    private static final String ARKNG_COMMAND = "arkng";
    private static final String ARKNG_COMMAND_DESCRIPTION = "The basic Arkng command";
    private static final String COULD_NOT_CREATE_COMMAND = "Could not create command";

    private final List<CommandProcessor> commandProcessors;

    @Autowired
    public SlashCommandRegisterServiceImpl(List<CommandProcessor> commandProcessors) {
        this.commandProcessors = commandProcessors;
    }

    @Override
    public void registerSlashCommands(@NonNull RestClient restClient) {
        ApplicationCommandRequest commandRequest = ApplicationCommandRequest.builder()
                .name(ARKNG_COMMAND)
                .description(ARKNG_COMMAND_DESCRIPTION)
                .addAllOptions(buildOptions())
                .build();

        long applicationId = restClient.getApplicationId().block();

        restClient.getApplicationService()
                .createGlobalApplicationCommand(applicationId, commandRequest)
                .doOnError(e -> LOGGER.warn(COULD_NOT_CREATE_COMMAND))
                .onErrorResume(e -> Mono.empty())
                .block();
    }

    private List<ApplicationCommandOptionData> buildOptions() {
        return commandProcessors.stream()
                .map(CommandProcessor::buildRequest)
                .collect(Collectors.toList());
    }
}
