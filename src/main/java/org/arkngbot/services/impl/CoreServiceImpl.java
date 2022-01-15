package org.arkngbot.services.impl;

import com.google.crypto.tink.aead.AeadConfig;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkngbot.eventprocessors.EventProcessor;
import org.arkngbot.services.CoreService;
import org.arkngbot.services.SlashCommandRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class CoreServiceImpl implements CoreService {

    private static final String STATUS_TEXT = "/arkng help";
    private static final Logger LOGGER = LogManager.getLogger(CoreServiceImpl.class);
    private static final String AEAD_ERROR = "Could not initialize AEAD. Exiting.";

    private final List<EventProcessor> eventProcessors;
    private final SlashCommandRegisterService slashCommandRegisterService;

    @Autowired
    public CoreServiceImpl (List<EventProcessor> eventProcessors, SlashCommandRegisterService slashCommandRegisterService) {
        this.eventProcessors = eventProcessors;
        this.slashCommandRegisterService = slashCommandRegisterService;
    }

    @Override
    public void initBot(String token) {
        registerAead();

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

    private void registerAead() {
        try {
            AeadConfig.register();
        }
        catch (GeneralSecurityException gse) {
            LOGGER.error(ExceptionUtils.getStackTrace(gse));
            LOGGER.error(AEAD_ERROR);
            System.exit(-1);
        }
    }
}
