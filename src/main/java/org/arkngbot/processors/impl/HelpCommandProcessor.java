package org.arkngbot.processors.impl;

import org.arkngbot.processors.CommandProcessor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HelpCommandProcessor implements CommandProcessor {

    private static final String HELP_TEXT = """
            Available commands:
            `/arkng wiki <query>`: Searches the UESP wiki and returns top five results. If you provide the namespace resulting in a direct hit (for example `/arkng wiki online:divayth fyr`), a direct link to the result will be returned.
            `/arkng wisdom`: Returns a random paragraph from a random TES lorebook.
            `/arkng ttc search <query>`: First tries to determine the item from the query using TTC's autocompletion feature, then returns a link to the search results of that item.
            `/arkng ttc price <query>`: First tries to determine the item from the query using TTC's autocompletion feature, then returns a price check for that item.
            `/arkng help`: Returns the list of all commands.""";
    private static final String HELP = "help";

    @Override
    public String processCommand(List<String> args) {
        return HELP_TEXT;
    }

    @Override
    public boolean supports(String command) {
        return HELP.equals(command);
    }
}
