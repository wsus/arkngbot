package org.arkngbot.eventprocessors;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

public abstract class AbstractEventProcessor<T extends Event> implements EventProcessor {

    @Override
    public void processEvent(GatewayDiscordClient client) {
        client.getEventDispatcher().on(getSupportedEventClass())
                .flatMap(this::processEvent)
                .subscribe();
    }

    public abstract Mono<?> processEvent(T event);

    public abstract Class<T> getSupportedEventClass();
}
