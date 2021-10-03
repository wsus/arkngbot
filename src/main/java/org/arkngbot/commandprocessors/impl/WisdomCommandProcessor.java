package org.arkngbot.commandprocessors.impl;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.UESPRandomLorebookParagraphExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * Processes a wisdom command.
 */
@Service
public class WisdomCommandProcessor implements CommandProcessor {

    private static final String WISDOM_COMMAND = "wisdom";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the wisdom :frowning:";
    private static final Logger LOGGER = LogManager.getLogger(WisdomCommandProcessor.class);
    private static final String WISDOM_COMMAND_DESCRIPTION = "Ask Arkng for a random lorebook paragraph";

    private final UESPRandomLorebookParagraphExtractorService uespRandomLorebookParagraphExtractorService;

    @Autowired
    public WisdomCommandProcessor(UESPRandomLorebookParagraphExtractorService uespRandomLorebookParagraphExtractorService) {
        this.uespRandomLorebookParagraphExtractorService = uespRandomLorebookParagraphExtractorService;
    }

    @NonNull
    @Override
    public String processCommand(ApplicationCommandInteractionOption command) {
        try {
            return uespRandomLorebookParagraphExtractorService.extractRandomLorebookParagraph();
        }
        catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            return ERROR_MESSAGE;
        }
    }

    @Override
    public boolean supports(String command) {
        return WISDOM_COMMAND.equals(command);
    }

    @NonNull
    @Override
    public ApplicationCommandOptionData buildRequest() {
        return ApplicationCommandOptionData.builder()
                .name(WISDOM_COMMAND)
                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                .description(WISDOM_COMMAND_DESCRIPTION)
                .build();
    }
}
