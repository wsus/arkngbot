package org.arkngbot.services;

import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import org.arkngbot.commandprocessors.CommandProcessor;
import org.arkngbot.services.impl.SlashCommandRegisterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class SlashCommandRegisterServiceTest {

    private static final String ARKNG_COMMAND = "arkng";
    private static final String ARKNG_COMMAND_DESCRIPTION = "The basic Arkng command";
    private static final long APP_ID = 1234L;
    private static final String MOCK_1 = "mock1";
    private static final String MOCK_DESC_1 = "mockDesc1";
    private static final String MOCK_2 = "mock2";
    private static final String MOCK_DESC_2 = "mockDesc2";

    private SlashCommandRegisterService slashCommandRegisterService;
    private CommandProcessor commandProcessorMock1;
    private CommandProcessor commandProcessorMock2;

    @BeforeEach
    public void setUp() {
        commandProcessorMock1 = mock(CommandProcessor.class);
        commandProcessorMock2 = mock(CommandProcessor.class);
        slashCommandRegisterService = new SlashCommandRegisterServiceImpl(Arrays.asList(commandProcessorMock1, commandProcessorMock2));
    }

    @Test
    public void shouldRegisterSlashCommands() {
        RestClient restClient = mock(RestClient.class);
        ApplicationService applicationService = mock(ApplicationService.class);
        prepareMocks(restClient, applicationService);

        ArgumentCaptor<ApplicationCommandRequest> captor = ArgumentCaptor.forClass(ApplicationCommandRequest.class);

        slashCommandRegisterService.registerSlashCommands(restClient);

        verify(applicationService).createGlobalApplicationCommand(eq(APP_ID), captor.capture());

        ApplicationCommandRequest request = captor.getValue();

        assertThat(request.name(), is(ARKNG_COMMAND));
        assertThat(request.description().get(), is(ARKNG_COMMAND_DESCRIPTION));
        assertThat(request.options().get().size(), is(2));
        verifyOption(request.options().get().get(0), MOCK_1, MOCK_DESC_1, ApplicationCommandOption.Type.SUB_COMMAND);
        verifyOption(request.options().get().get(1), MOCK_2, MOCK_DESC_2, ApplicationCommandOption.Type.SUB_COMMAND_GROUP);
    }

    private void verifyOption(ApplicationCommandOptionData option, String name, String desc, ApplicationCommandOption.Type type) {
        assertThat(option.name(), is(name));
        assertThat(option.description(), is(desc));
        assertThat(option.type(), is(type.getValue()));
    }

    private void prepareMocks(RestClient restClient, ApplicationService applicationService) {
        when(restClient.getApplicationService()).thenReturn(applicationService);
        when(restClient.getApplicationId()).thenReturn(Mono.just(APP_ID));
        when(applicationService.createGlobalApplicationCommand(anyLong(), any())).thenReturn(Mono.empty());
        when(commandProcessorMock1.buildRequest()).thenReturn(ApplicationCommandOptionData.builder()
                .name(MOCK_1)
                .description(MOCK_DESC_1)
                .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                .build());
        when(commandProcessorMock2.buildRequest()).thenReturn(ApplicationCommandOptionData.builder()
                .name(MOCK_2)
                .description(MOCK_DESC_2)
                .type(ApplicationCommandOption.Type.SUB_COMMAND_GROUP.getValue())
                .build());
    }
}
