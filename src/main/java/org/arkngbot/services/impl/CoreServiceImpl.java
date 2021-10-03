package org.arkngbot.services.impl;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import org.arkngbot.eventprocessors.EventProcessor;
import org.arkngbot.services.CoreService;
import org.arkngbot.services.SlashCommandRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoreServiceImpl implements CoreService {

    private static final String STATUS_TEXT = "/arkng help";

    private final List<EventProcessor> eventProcessors;
    private final SlashCommandRegisterService slashCommandRegisterService;

    @Autowired
    public CoreServiceImpl (List<EventProcessor> eventProcessors, SlashCommandRegisterService slashCommandRegisterService) {
        this.eventProcessors = eventProcessors;
        this.slashCommandRegisterService = slashCommandRegisterService;
    }

    @Override
    public void initBot(String token) {

        GatewayDiscordClient client = DiscordClientBuilder.create(token)
                .build()
                .gateway()
                .setEnabledIntents(IntentSet.of(Intent.GUILD_MEMBERS))
                .login()
                .block();

        client.updatePresence(ClientPresence.online(ClientActivity.playing(STATUS_TEXT))).block();

        slashCommandRegisterService.registerSlashCommands(client.getRestClient());
        eventProcessors.forEach(p -> p.processEvent(client));

        client.onDisconnect().block();
    }
}
