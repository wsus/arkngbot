package org.arkngbot.eventprocessors.impl;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.WebhookMessageEditRequest;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.interaction.InteractionResponse;
import org.arkngbot.eventprocessors.AbstractEventProcessor;
import org.arkngbot.services.CommandProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class ChatInputInteractionEventProcessor extends AbstractEventProcessor<ChatInputInteractionEvent> {

    private final CommandProcessingService commandProcessingService;

    @Autowired
    public ChatInputInteractionEventProcessor(CommandProcessingService commandProcessingService) {
        this.commandProcessingService = commandProcessingService;
    }

    @Override
    public Mono<MessageData> processEvent(ChatInputInteractionEvent event) {
        event.deferReply().block();
        InteractionResponse interactionResponse = event.getInteractionResponse();
        Optional<String> responseOpt = event.getInteraction().getCommandInteraction()
                .map(commandProcessingService::processCommand);
        WebhookMessageEditRequest editRequest = WebhookMessageEditRequest.builder().content(Possible.of(responseOpt)).build();
        return interactionResponse.editInitialResponse(editRequest);
    }

    @Override
    public Class<ChatInputInteractionEvent> getSupportedEventClass() {
        return ChatInputInteractionEvent.class;
    }
}
