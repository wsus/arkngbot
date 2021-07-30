package org.arkngbot.eventprocessors.impl;

import discord4j.core.event.domain.interaction.SlashCommandEvent;
import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.WebhookMessageEditRequest;
import discord4j.rest.interaction.InteractionResponse;
import org.arkngbot.eventprocessors.AbstractEventProcessor;
import org.arkngbot.services.CommandProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SlashCommandEventProcessor extends AbstractEventProcessor<SlashCommandEvent> {

    private static final String ARKNG_COMMAND = "/arkng ";

    private CommandProcessingService commandProcessingService;

    @Autowired
    public SlashCommandEventProcessor(CommandProcessingService commandProcessingService) {
        this.commandProcessingService = commandProcessingService;
    }

    @Override
    public Mono<MessageData> processEvent(SlashCommandEvent event) {
        event.acknowledge().block();
        InteractionResponse interactionResponse = event.getInteractionResponse();
        String response = commandProcessingService.processCommand(event.getInteraction().getCommandInteraction().get());
        WebhookMessageEditRequest editRequest = WebhookMessageEditRequest.builder().content(response).build();
        return interactionResponse.editInitialResponse(editRequest);
    }

    @Override
    public Class<SlashCommandEvent> getSupportedEventClass() {
        return SlashCommandEvent.class;
    }
}
