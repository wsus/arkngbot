package org.arkngbot.eventprocessors.impl;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arkngbot.eventprocessors.AbstractEventProcessor;
import org.arkngbot.services.impl.CoreServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReadyEventProcessor extends AbstractEventProcessor<ReadyEvent> {

    private static final String READY_MESSAGE = "Logged in as %s#%s%n";
    private static final Logger LOGGER = LogManager.getLogger(CoreServiceImpl.class);

    @Override
    public Mono<Void> processEvent(ReadyEvent event) {
        User self = event.getSelf();
        LOGGER.info(String.format(READY_MESSAGE, self.getUsername(), self.getDiscriminator()));

        return Mono.empty();
    }

    @Override
    public Class<ReadyEvent> getSupportedEventClass() {
        return ReadyEvent.class;
    }
}
