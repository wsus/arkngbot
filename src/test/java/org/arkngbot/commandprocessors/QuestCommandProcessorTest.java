package org.arkngbot.commandprocessors;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.arkngbot.commandprocessors.impl.QuestCommandProcessor;
import org.arkngbot.services.QuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuestCommandProcessorTest {

    private static final String QUEST_COMMAND = "quest";
    private static final String QUEST_COMMAND_DESCRIPTION = "Answer a New Life Treasure Hunt riddle and get the next one";
    private static final String QUESTION_NUMBER_OPTION_NAME = "question_number";
    private static final String QUESTION_NUMBER_OPTION_DESCRIPTION = "The number of the question you want to answer, starting from 1.";
    private static final String ANSWER_OPTION_NAME = "answer";
    private static final String ANSWER_OPTION_DESCRIPTION = "The answer to the question identified by the question number. Case insensitive.";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not process your request :frowning:";
    private static final String QUEST_ANSWER = "This is your next quest!";
    private static final String ANSWER_1 = "answer1";

    private CommandProcessor commandProcessor;

    private QuestService questService;

    @BeforeEach
    public void setUp() {
        questService = mock(QuestService.class);
        commandProcessor = new QuestCommandProcessor(questService);
    }

    @Test
    public void shouldReturnAnswer() throws Exception {
        ApplicationCommandInteractionOption command = buildCommand();
        when(questService.processRequest(1, ANSWER_1)).thenReturn(QUEST_ANSWER);

        String result = commandProcessor.processCommand(command);

        assertThat(result, is(QUEST_ANSWER));
    }

    @Test
    public void shouldNotReturnAnswerExceptionCaught() throws Exception {
        ApplicationCommandInteractionOption command = buildCommand();
        when(questService.processRequest(1, ANSWER_1)).thenThrow(new IOException());

        String result = commandProcessor.processCommand(command);

        assertThat(result, is(ERROR_MESSAGE));
    }

    @Test
    public void shouldSupportCommand() {
        boolean supports = commandProcessor.supports(QUEST_COMMAND);

        assertThat(supports, is(true));
    }

    @Test
    public void shouldNotSupportCommand() {
        boolean supports = commandProcessor.supports(ERROR_MESSAGE);

        assertThat(supports, is(false));
    }

    @Test
    public void shouldBuildRequest() {
        ApplicationCommandOptionData request = commandProcessor.buildRequest();

        assertThat(request.name(), is(QUEST_COMMAND));
        assertThat(request.description(), is(QUEST_COMMAND_DESCRIPTION));
        assertThat(request.type(), is(ApplicationCommandOption.Type.SUB_COMMAND.getValue()));

        ApplicationCommandOptionData raceOption = request.options().get().get(0);
        assertThat(raceOption.name(), is(QUESTION_NUMBER_OPTION_NAME));
        assertThat(raceOption.description(), is(QUESTION_NUMBER_OPTION_DESCRIPTION));
        assertThat(raceOption.type(), is(ApplicationCommandOption.Type.INTEGER.getValue()));
        assertThat(raceOption.required().get(), is(true));

        ApplicationCommandOptionData sexOption = request.options().get().get(1);
        assertThat(sexOption.name(), is(ANSWER_OPTION_NAME));
        assertThat(sexOption.description(), is(ANSWER_OPTION_DESCRIPTION));
        assertThat(sexOption.type(), is(ApplicationCommandOption.Type.STRING.getValue()));
        assertThat(sexOption.required().get(), is(true));
    }

    @Test
    public void shouldReplyInPrivate(){
        boolean privateReply = commandProcessor.privateReply();
        assertThat(privateReply, is(true));
    }

    private ApplicationCommandInteractionOption buildCommand() {
        ApplicationCommandInteractionOption command = mock(ApplicationCommandInteractionOption.class);

        ApplicationCommandInteractionOption numberOption = buildOption(1);
        ApplicationCommandInteractionOption answerOption = buildOption(ANSWER_1);

        when(command.getOption(QUESTION_NUMBER_OPTION_NAME)).thenReturn(Optional.of(numberOption));
        when(command.getOption(ANSWER_OPTION_NAME)).thenReturn(Optional.of(answerOption));

        return command;
    }

    private ApplicationCommandInteractionOption buildOption(String value) {
        ApplicationCommandInteractionOption option = mock(ApplicationCommandInteractionOption.class);
        ApplicationCommandInteractionOptionValue optionValue = mock(ApplicationCommandInteractionOptionValue.class);
        when(optionValue.asString()).thenReturn(value);
        when(option.getValue()).thenReturn(Optional.of(optionValue));

        return option;
    }

    private ApplicationCommandInteractionOption buildOption(long value) {
        ApplicationCommandInteractionOption option = mock(ApplicationCommandInteractionOption.class);
        ApplicationCommandInteractionOptionValue optionValue = mock(ApplicationCommandInteractionOptionValue.class);
        when(optionValue.asLong()).thenReturn(value);
        when(option.getValue()).thenReturn(Optional.of(optionValue));

        return option;
    }
}
