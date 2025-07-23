package com.damvih.fsm.action;

import com.damvih.dto.ParticipantDto;
import com.damvih.dto.UserSettingsDto;
import com.damvih.fsm.state.ResultEvent;
import com.damvih.fsm.state.ResultState;
import com.damvih.message.TelegramMessageFactory;
import com.damvih.message.TelegramOutgoingMessage;
import com.damvih.service.CalculationResultService;
import com.damvih.service.MessageDispatcherService;
import com.damvih.util.ResultTextFormatter;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

@Component
public class ConfirmationReceivedResultAction extends ResultAction {

    private final CalculationResultService calculationResultService;
    private final ResultTextFormatter resultTextFormatter;

    public ConfirmationReceivedResultAction(MessageDispatcherService messageDispatcherService, TelegramMessageFactory telegramMessageFactory, CalculationResultService calculationResultService, ResultTextFormatter resultTextFormatter) {
        super(messageDispatcherService, telegramMessageFactory);
        this.calculationResultService = calculationResultService;
        this.resultTextFormatter = resultTextFormatter;
    }

    @Override
    public void execute(StateContext<ResultState, ResultEvent> stateContext) {
        Update update = (Update) stateContext.getMessageHeader("payload");

        Long userId = update.getMessage().getChatId();
        UserSettingsDto userSettingsDto = getUserSettingsDto(stateContext.getExtendedState());

        String loadingMessage = "Начинается определение результата. Подождите, процесс может занимать до нескольких минут.";
        getMessageDispatcherService().dispatch(
                new TelegramOutgoingMessage(
                        getTelegramMessageFactory().create(userId, loadingMessage)
                )
        );

        List<ParticipantDto> participants = calculationResultService.calculate(userSettingsDto.getGroupId(), userSettingsDto.getAlbumId());
        participants = calculationResultService.sort(participants);
        List<ParticipantDto> winners = calculationResultService.getWinners(participants);
        sendResultMessage(userId, participants, winners);
    }

    private UserSettingsDto getUserSettingsDto(ExtendedState extendedState) {
        return new UserSettingsDto(
                extendedState.get("groupId", Long.class),
                extendedState.get("albumId", Long.class)
        );
    }

    private void sendResultMessage(Long userId, List<ParticipantDto> participants, List<ParticipantDto> winners) {
        MessageDispatcherService messageDispatcherService = getMessageDispatcherService();
        TelegramMessageFactory telegramMessageFactory = getTelegramMessageFactory();

        SendMessage sortedParticipantsResultMessage = telegramMessageFactory.create(userId, resultTextFormatter.formatDateTimeNowText() + resultTextFormatter.formatParticipants(participants));
        SendMessage winnersMessage = telegramMessageFactory.create(userId, resultTextFormatter.formatWinners(winners));

        messageDispatcherService.dispatch(new TelegramOutgoingMessage(sortedParticipantsResultMessage));
        messageDispatcherService.dispatch(new TelegramOutgoingMessage(winnersMessage));
    }

}
