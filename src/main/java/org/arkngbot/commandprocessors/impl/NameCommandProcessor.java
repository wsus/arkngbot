package org.arkngbot.commandprocessors.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.datastructures.NameAware;
import org.arkngbot.datastructures.enums.TESRace;
import org.arkngbot.datastructures.enums.TESSex;
import org.arkngbot.services.LoreNameGeneratorService;
import org.arkngbot.services.impl.ArticleSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class NameCommandProcessor implements CommandProcessor {

    private static final String NAME_COMMAND = "name";
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
        Random randomizer = new Random();

        TESRace race = retrieveEnumValueOrRandomize(args, TESRace.values(), 0, randomizer);
        if (race == null) {
            return buildInvalidRaceMessage();
        }
        TESSex sex = retrieveEnumValueOrRandomize(args, TESSex.values(), 1, randomizer);
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

    private <T extends NameAware> T retrieveEnumValueOrRandomize(List<String> args, T[] values, int index, Random randomizer) {
        if (args != null && args.size() > index) {
            return Arrays.stream(values)
                    .filter(v -> v.getName().equalsIgnoreCase(args.get(index)))
                    .findFirst()
                    .orElse(null);
        }
        else {
            return getRandomElement(Arrays.asList(values), randomizer);
        }
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

    private <T> T getRandomElement(List<T> collection, Random randomizer) {
        return collection.get(randomizer.nextInt(collection.size()));
    }
}
