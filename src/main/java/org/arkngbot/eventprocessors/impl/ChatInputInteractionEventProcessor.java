package org.arkngbot.eventprocessors.impl;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.json.MessageCreateRequest;
import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.WebhookMessageEditRequest;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.interaction.InteractionResponse;
import discord4j.rest.util.MultipartRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.arkngbot.eventprocessors.AbstractEventProcessor;
import org.arkngbot.services.CommandProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class ChatInputInteractionEventProcessor extends AbstractEventProcessor<ChatInputInteractionEvent> {

    private static final String PRIVATE_MESSAGE_RESPONSE = "%s, check your DM channel!";
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
        boolean privateReply = event.getInteraction().getCommandInteraction()
                .map(commandProcessingService::checkPrivateReply).get();

        if (privateReply) {
            responseOpt = Optional.of(replyInPrivateChannel(event, responseOpt));
        }
        WebhookMessageEditRequest editRequest = WebhookMessageEditRequest.builder().content(Possible.of(responseOpt)).build();
        return interactionResponse.editInitialResponse(editRequest);
    }

    private String replyInPrivateChannel(ChatInputInteractionEvent event, Optional<String> responseOpt) {
        User requester = event.getInteraction().getUser();
        Long requesterPrivateChannelId = requester.getPrivateChannel()
                .map(Channel::getId)
                .map(Snowflake::asLong)
                .block();
        if (isNotPrivateChannelAlready(event, requesterPrivateChannelId)) {
            MessageCreateRequest messageCreateRequest = MessageCreateRequest.builder().content(responseOpt.get()).build();
            MultipartRequest multipartRequest = MultipartRequest.ofRequest(messageCreateRequest);
            event.getClient().getRestClient().getChannelService().createMessage(requesterPrivateChannelId, multipartRequest).block();

            String username = resolveUsername(requester, event.getInteraction().getGuild());
            return String.format(PRIVATE_MESSAGE_RESPONSE, username);
        }
        return responseOpt.get();
    }

    private String resolveUsername(User user, Mono<Guild> guild) {
        return Optional.ofNullable(guild.map(Guild::getId)
                        .flatMap(user::asMember)
                        .map(PartialMember::getNickname)
                        .block())
                .flatMap(nameOpt -> nameOpt)
                .orElse(user.getUsername());
    }

    private Long retrieveRequesterPrivateChannelId(ChatInputInteractionEvent event) {
        return event.getInteraction().getUser().getPrivateChannel()
                .map(Channel::getId)
                .map(Snowflake::asLong)
                .block();
    }

    private boolean isNotPrivateChannelAlready(ChatInputInteractionEvent event, Long privateChannelId) {
        Long eventChannelId = event.getInteraction().getChannel()
                .map(Channel::getId)
                .map(Snowflake::asLong)
                .block();
        return ObjectUtils.notEqual(privateChannelId, eventChannelId);
    }

    @Override
    public Class<ChatInputInteractionEvent> getSupportedEventClass() {
        return ChatInputInteractionEvent.class;
    }
}
