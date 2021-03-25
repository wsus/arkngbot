package org.arkngbot.commandprocessors;

import org.arkngbot.commandprocessors.impl.NameCommandProcessor;
import org.arkngbot.datastructures.enums.TESRace;
import org.arkngbot.datastructures.enums.TESSex;
import org.arkngbot.services.LoreNameGeneratorService;
import org.arkngbot.services.impl.ArticleSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NameCommandProcessorTest {

    private static final String NAME_COMMAND = "name";
    private static final String TOO_FEW_ARGUMENTS = "This command requires two arguments!";
    private static final String GENERATED_NAME_PATTERN = "Generated name of %s %s %s:\n%s";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not generate the name :frowning:";
    private static final String INVALID_RACE_VALUE = "Invalid race value! Valid values are:";
    private static final String INVALID_SEX_VALUE = "Invalid sex value! Valid values are:";
    private static final String VALUE_PATTERN = "\n`%s`";
    private static final String NAME = "John Doe";
    private static final String INDEF_ARTICLE_A = "a";
    private static final String MALE = "male";
    private static final String BRETON = "breton";
    private static final String INVALID_STRING = "invalidString";

    private CommandProcessor commandProcessor;

    private LoreNameGeneratorService loreNameGeneratorService;

    private ArticleSupport articleSupport;

    @BeforeEach
    public void setUp() {
        loreNameGeneratorService = mock(LoreNameGeneratorService.class);
        articleSupport = mock(ArticleSupport.class);

        commandProcessor = new NameCommandProcessor(loreNameGeneratorService, articleSupport);
    }

    @Test
    public void shouldProcessNameCommand() throws Exception {
        when(loreNameGeneratorService.generateLoreName(TESRace.BRETON, TESSex.MALE)).thenReturn(NAME);
        when(articleSupport.determineIndefiniteArticle(TESRace.BRETON.getName())).thenReturn(INDEF_ARTICLE_A);

        String result = commandProcessor.processCommand(Arrays.asList(BRETON, MALE));

        assertThat(result, is(String.format(GENERATED_NAME_PATTERN,
                INDEF_ARTICLE_A, TESRace.BRETON.getName(), TESSex.MALE.getName(), NAME)));
    }

    @Test
    public void shouldNotProcessNameCommandTooFewArguments() {
        String result = commandProcessor.processCommand(Collections.singletonList(BRETON));

        assertThat(result, is(TOO_FEW_ARGUMENTS));
    }

    @Test
    public void shouldNotProcessNameCommandInvalidRace() {
        String result = commandProcessor.processCommand(Arrays.asList(INVALID_STRING, MALE));

        assertThat(result, is(buildInvalidRaceMessage()));
    }

    @Test
    public void shouldNotProcessNameCommandInvalidSex() {
        String result = commandProcessor.processCommand(Arrays.asList(BRETON, INVALID_STRING));

        assertThat(result, is(buildInvalidSexMessage()));
    }

    @Test
    public void shouldNotProcessNameCommandExceptionCaught() throws Exception {
        when(loreNameGeneratorService.generateLoreName(TESRace.BRETON, TESSex.MALE)).thenThrow(new IOException());

        String result = commandProcessor.processCommand(Arrays.asList(BRETON, MALE));

        assertThat(result, is(ERROR_MESSAGE));
    }

    @Test
    public void shouldSupportCommand() {
        boolean supports = commandProcessor.supports(NAME_COMMAND);

        assertThat(supports, is(true));
    }

    @Test
    public void shouldNotSupportCommand() {
        boolean supports = commandProcessor.supports(NAME);

        assertThat(supports, is(false));
    }

    private String buildInvalidRaceMessage() {
        StringBuilder builder = new StringBuilder(INVALID_RACE_VALUE);
        Arrays.stream(TESRace.values())
                .forEach(v -> builder.append(String.format(VALUE_PATTERN, v.getName())));
        return builder.toString();
    }

    private String buildInvalidSexMessage() {
        StringBuilder builder = new StringBuilder(INVALID_SEX_VALUE);
        Arrays.stream(TESSex.values())
                .forEach(v -> builder.append(String.format(VALUE_PATTERN, v.getName())));
        return builder.toString();
    }
}
