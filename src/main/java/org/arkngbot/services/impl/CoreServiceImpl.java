package org.arkngbot.services.impl;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkngbot.services.CommandProcessingService;
import org.arkngbot.services.CoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CoreServiceImpl implements CoreService {

    private static final String READY_MESSAGE = "Logged in as %s#%s%n";
    private static final String ARKNG_COMMAND = "/arkng ";
    private static final Logger LOGGER = LogManager.getLogger(CoreServiceImpl.class);
    private static final String STATUS_TEXT = "/arkng help";

    private final CommandProcessingService commandProcessingService;

    @Autowired
    public CoreServiceImpl (CommandProcessingService commandProcessingService) {
        this.commandProcessingService = commandProcessingService;
    }

    @Override
    public void initBot(String token) {

        GatewayDiscordClient client = DiscordClientBuilder.create(token)
                .build()
                .login()
                .block();

        client.updatePresence(Presence.online(Activity.playing(STATUS_TEXT))).block();

        reactOnReadyEvent(client);
        reactOnMessageCreatedEvent(client);

        client.onDisconnect().block();
    }

    private void reactOnReadyEvent(GatewayDiscordClient client) {
        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(this::processReadyEvent);
    }

    private void reactOnMessageCreatedEvent(GatewayDiscordClient client) {
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap(this::processMessageCreatedEvent)
                .subscribe();
    }

    private void processReadyEvent(ReadyEvent event) {
        User self = event.getSelf();
        LOGGER.info(String.format(READY_MESSAGE, self.getUsername(), self.getDiscriminator()));
    }

    private Mono<Message> processMessageCreatedEvent(MessageCreateEvent event) {
        String message = event.getMessage().getContent();

        if (message.startsWith(ARKNG_COMMAND)) {
            String response = commandProcessingService.processCommand(message);
            return event.getMessage().getChannel()
                    .flatMap(c -> c.createMessage(response));
        }

        return Mono.empty();
    }
}
