package org.arkngbot.commandprocessors.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.UESPRandomLorebookParagraphExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Processes a /wisdom command.
 */
@Service
public class WisdomCommandProcessor implements CommandProcessor {

    private static final String WISDOM_COMMAND = "wisdom";
    private static final String ERROR_MESSAGE = "Something went wrong. Could not retrieve the wisdom :frowning:";
    private static final Logger LOGGER = LogManager.getLogger(WisdomCommandProcessor.class);

    private final UESPRandomLorebookParagraphExtractorService uespRandomLorebookParagraphExtractorService;

    @Autowired
    public WisdomCommandProcessor(UESPRandomLorebookParagraphExtractorService uespRandomLorebookParagraphExtractorService) {
        this.uespRandomLorebookParagraphExtractorService = uespRandomLorebookParagraphExtractorService;
    }

    @Override
    public String processCommand(List<String> args) {
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
}
