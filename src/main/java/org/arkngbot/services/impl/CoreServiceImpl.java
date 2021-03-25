package org.arkngbot.services.impl;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import org.arkngbot.eventprocessors.EventProcessor;
import org.arkngbot.services.CoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoreServiceImpl implements CoreService {

    private static final String STATUS_TEXT = "/arkng help";

    private final List<EventProcessor> eventProcessors;

    @Autowired
    public CoreServiceImpl (List<EventProcessor> eventProcessors) {
        this.eventProcessors = eventProcessors;
    }

    @Override
    public void initBot(String token) {

        GatewayDiscordClient client = DiscordClientBuilder.create(token)
                .build()
                .login()
                .block();

        client.updatePresence(Presence.online(Activity.playing(STATUS_TEXT))).block();

        eventProcessors.forEach(p -> p.processEvent(client));

        client.onDisconnect().block();
    }
}
