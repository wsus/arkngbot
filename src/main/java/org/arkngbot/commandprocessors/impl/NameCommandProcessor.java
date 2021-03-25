package org.arkngbot.commandprocessors.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.datastructures.enums.TESRace;
import org.arkngbot.datastructures.enums.TESSex;
import org.arkngbot.services.LoreNameGeneratorService;
import org.arkngbot.services.impl.ArticleSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class NameCommandProcessor implements CommandProcessor {

    private static final String NAME_COMMAND = "name";
    private static final String TOO_FEW_ARGUMENTS = "This command requires two arguments!";
    private static final String GENERATED_NAME_PATTERN = "Generated name of %s %s %s:\n%s";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not generate the name :frowning:";
    private static final String INVALID_RACE_VALUE = "Invalid race value! Valid values are:";
    private static final String INVALID_SEX_VALUE = "Invalid sex value! Valid values are:";
    private static final Logger LOGGER = LogManager.getLogger(NameCommandProcessor.class);
    private static final String VALUE_PATTERN = "\n`%s`";

    private final LoreNameGeneratorService loreNameGeneratorService;

    private final ArticleSupport articleSupport;

    @Autowired
    public NameCommandProcessor(LoreNameGeneratorService loreNameGeneratorService, ArticleSupport articleSupport) {
        this.loreNameGeneratorService = loreNameGeneratorService;
        this.articleSupport = articleSupport;
    }

    @Override
    public String processCommand(List<String> args) {
        if (args == null || args.size() < 2) {
            return TOO_FEW_ARGUMENTS;
        }

        TESRace race = retrieveRaceCaseInsensitive(args.get(0));
        if (race == null) {
            return buildInvalidRaceMessage();
        }
        TESSex sex = retrieveSexCaseInsensitive(args.get(1));
        if (sex == null) {
            return buildInvalidSexMessage();
        }

        try {
            String generatedName = loreNameGeneratorService.generateLoreName(race, sex);
            return String.format(GENERATED_NAME_PATTERN,
                    articleSupport.determineIndefiniteArticle(race.getName()), race.getName(), sex.getName(), generatedName);
        }
        catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            return ERROR_MESSAGE;
        }
    }

    @Override
    public boolean supports(String command) {
        return NAME_COMMAND.equals(command);
    }

    private TESRace retrieveRaceCaseInsensitive(String string) {
        return Arrays.stream(TESRace.values())
                .filter(v -> v.getName().equalsIgnoreCase(string))
                .findFirst()
                .orElse(null);
    }

    private TESSex retrieveSexCaseInsensitive(String string) {
        return Arrays.stream(TESSex.values())
                .filter(v -> v.getName().equalsIgnoreCase(string))
                .findFirst()
                .orElse(null);
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
