package org.arkngbot.commandprocessors.impl;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.impl.PropertiesSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class VersionCommandProcessor implements CommandProcessor {

    private static final String VERSION_MESSAGE_PATTERN = "My current version is %s.";
    private static final String VERSION_PROPERTY_KEY = "arkngbot.version";
    private static final String VERSION_COMMAND = "version";
    private static final String VERSION_COMMAND_DESCRIPTION = "See Arkng's current version";

    private PropertiesSupport propertiesSupport;

    @Autowired
    public VersionCommandProcessor(PropertiesSupport propertiesSupport) {
        this.propertiesSupport = propertiesSupport;
    }

    @NonNull
    @Override
    public String processCommand(ApplicationCommandInteractionOption command) {
        return String.format(VERSION_MESSAGE_PATTERN, propertiesSupport.getProperty(VERSION_PROPERTY_KEY));
    }

    @Override
    public boolean supports(String command) {
        return VERSION_COMMAND.equals(command);
    }

    @NonNull
    @Override
    public ApplicationCommandOptionData buildRequest() {
        return ApplicationCommandOptionData.builder()
                .name(VERSION_COMMAND)
                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                .description(VERSION_COMMAND_DESCRIPTION)
                .build();
    }
}
