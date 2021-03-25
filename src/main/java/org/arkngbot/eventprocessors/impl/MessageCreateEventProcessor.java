package org.arkngbot.eventprocessors.impl;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.arkngbot.eventprocessors.AbstractEventProcessor;
import org.arkngbot.services.CommandProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MessageCreateEventProcessor extends AbstractEventProcessor<MessageCreateEvent> {

    private static final String ARKNG_COMMAND = "/arkng ";

    private CommandProcessingService commandProcessingService;

    @Autowired
    public MessageCreateEventProcessor(CommandProcessingService commandProcessingService) {
        this.commandProcessingService = commandProcessingService;
    }

    @Override
    public Mono<Message> processEvent(MessageCreateEvent event) {
        String message = event.getMessage().getContent();

        if (message.startsWith(ARKNG_COMMAND)) {
            String response = commandProcessingService.processCommand(message);
            return event.getMessage().getChannel()
                    .flatMap(c -> c.createMessage(response));
        }

        return Mono.empty();
    }

    @Override
    public Class<MessageCreateEvent> getSupportedEventClass() {
        return MessageCreateEvent.class;
    }
}
