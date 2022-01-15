package org.arkngbot.commandprocessors.impl;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class QuestCommandProcessor implements CommandProcessor {

    private static final String QUEST_COMMAND = "quest";
    private static final Logger LOGGER = LogManager.getLogger(QuestCommandProcessor.class);
    private static final String QUEST_COMMAND_DESCRIPTION = "Answer a New Life Treasure Hunt riddle and get the next one";
    private static final String QUESTION_NUMBER_OPTION_NAME = "question_number";
    private static final String QUESTION_NUMBER_OPTION_DESCRIPTION = "The number of the question you want to answer, starting from 1.";
    private static final String ANSWER_OPTION_NAME = "answer";
    private static final String ANSWER_OPTION_DESCRIPTION = "The answer to the question identified by the question number. Case insensitive.";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not process your request :frowning:";

    private QuestService questService;

    @Autowired
    public QuestCommandProcessor (QuestService questService){
        this.questService = questService;
    }

    @NonNull
    @Override
    public String processCommand(@NonNull ApplicationCommandInteractionOption command) {
        long number = command.getOption(QUESTION_NUMBER_OPTION_NAME)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asLong)
                .get();
        String answer = command.getOption(ANSWER_OPTION_NAME)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();

        try {
            return questService.processRequest(number, answer);
        }
        catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            return ERROR_MESSAGE;
        }
    }

    @Override
    public boolean supports(@NonNull String command) {
        return QUEST_COMMAND.equals(command);
    }

    @NonNull
    @Override
    public ApplicationCommandOptionData buildRequest() {
        return ApplicationCommandOptionData.builder()
                .name(QUEST_COMMAND)
                .description(QUEST_COMMAND_DESCRIPTION)
                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                .addOption(buildOption(QUESTION_NUMBER_OPTION_NAME, QUESTION_NUMBER_OPTION_DESCRIPTION, ApplicationCommandOption.Type.INTEGER))
                .addOption(buildOption(ANSWER_OPTION_NAME, ANSWER_OPTION_DESCRIPTION, ApplicationCommandOption.Type.STRING))
                .build();
    }

    private ApplicationCommandOptionData buildOption(String name, String desc, ApplicationCommandOption.Type type) {
        return ApplicationCommandOptionData.builder()
                .name(name)
                .description(desc)
                .type(type.getValue())
                .required(true)
                .build();
    }

    @Override
    public boolean privateReply() {
        return true;
    }
}
