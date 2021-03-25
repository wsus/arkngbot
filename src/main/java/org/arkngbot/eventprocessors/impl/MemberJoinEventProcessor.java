package org.arkngbot.eventprocessors.impl;

import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import org.arkngbot.eventprocessors.AbstractEventProcessor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MemberJoinEventProcessor extends AbstractEventProcessor<MemberJoinEvent> {

    private static final String GREETING = "Hello %s! Welcome to %s!";
    private static final String GENERIC_RULES_CLAUSE = "\nPlease make sure to visit the local information channel and familiarize yourself with the guild rules. If you need help, ask the guild officers.";
    private static final String RULES_CLAUSE = "\nPlease make sure to visit %s and familiarize yourself with the guild rules. If you need help, ask the guild officers.";

    @Override
    public Mono<Message> processEvent(MemberJoinEvent event) {
        String guildName = event.getGuild()
                .map(Guild::getName)
                .block();
        String memberName = event.getMember().getDisplayName();
        String rulesChannelName = event.getGuild()
                .flatMap(Guild::getRulesChannel)
                .map(c -> c.getName())
                .block();

        String greeting = buildGreeting(guildName, memberName, rulesChannelName);
        return event.getGuild()
                .flatMap(Guild::getSystemChannel)
                .flatMap(c -> c.createMessage(greeting));
    }

    private String buildGreeting(String guildName, String memberName, String rulesChannelName) {
        StringBuilder builder = new StringBuilder(String.format(GREETING, memberName, guildName));

        if (rulesChannelName != null) {
            builder.append(String.format(RULES_CLAUSE, rulesChannelName));
        }
        else {
            builder.append(GENERIC_RULES_CLAUSE);
        }

        return builder.toString();
    }

    @Override
    public Class<MemberJoinEvent> getSupportedEventClass() {
        return MemberJoinEvent.class;
    }
}
