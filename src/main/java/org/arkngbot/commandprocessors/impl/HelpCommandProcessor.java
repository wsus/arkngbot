package org.arkngbot.commandprocessors.impl;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class HelpCommandProcessor implements CommandProcessor {

    private static final String HELP_TEXT = "Available commands:"
            + "\n`/arkng wiki <query>`: Searches the UESP wiki and returns top five results. If you provide the namespace resulting in a direct hit (for example `/arkng wiki online:divayth fyr`), a direct link to the result will be returned."
            + "\n`/arkng wisdom`: Returns a random paragraph from a random TES lorebook."
            + "\n`/arkng ttc search <query>`: First tries to determine the item from the query using TTC's autocompletion feature, then returns a link to the search results of that item."
            + "\n`/arkng ttc price <query>`: First tries to determine the item from the query using TTC's autocompletion feature, then returns a price check for that item."
            + "\n`/arkng help`: Returns the list of all commands."
            + "\n`/arkng name [race] [sex]`: Generates a random lore-friendly Elder Scrolls name out of the name pool from the games and lore collected by the UESP. Whether a family name will be generated depends on the race. `ashlander` and `reachman` are available as races since they have different naming patterns. If sex or race are not given, they will be chosen randomly."
            + "\n`/arkng version`: Shows the current version of Arkng."
            + "\n`/arkng pledges`: Shows the current Undaunted pledges."
            + "\n`/arkng luxury`: Shows the current luxury furnishings if they are available."
            + "\n`/arkng golden`: Shows the current golden merchant items if they are available."
            + "\n`/arkng servers`: Displays the current status of the megaservers.";
    private static final String HELP = "help";
    private static final String HELP_COMMAND_DESCRIPTION = "See a list of available commands";

    @NonNull
    @Override
    public String processCommand(@NonNull ApplicationCommandInteractionOption command) {
        return HELP_TEXT;
    }

    @Override
    public boolean supports(String command) {
        return HELP.equals(command);
    }

    @NonNull
    @Override
    public ApplicationCommandOptionData buildRequest() {
        return ApplicationCommandOptionData.builder()
                .name(HELP)
                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                .description(HELP_COMMAND_DESCRIPTION)
                .build();
    }
}
