package org.arkngbot.commandprocessors.impl;

import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.impl.PropertiesSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VersionCommandProcessor implements CommandProcessor {

    private static final String VERSION_MESSAGE_PATTERN = "My current version is %s.";
    private static final String VERSION_PROPERTY_KEY = "arkngbot.version";
    private static final String VERSION_COMMAND = "version";

    private PropertiesSupport propertiesSupport;

    @Autowired
    public VersionCommandProcessor(PropertiesSupport propertiesSupport) {
        this.propertiesSupport = propertiesSupport;
    }

    @Override
    public String processCommand(List<String> args) {
        return String.format(VERSION_MESSAGE_PATTERN, propertiesSupport.getProperty(VERSION_PROPERTY_KEY));
    }

    @Override
    public boolean supports(String command) {
        return VERSION_COMMAND.equals(command);
    }
}
