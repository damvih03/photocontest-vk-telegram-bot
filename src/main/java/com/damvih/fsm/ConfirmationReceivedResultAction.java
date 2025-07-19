package com.damvih.fsm;

import com.damvih.dto.ParticipantDto;
import com.damvih.dto.UserSettingsDto;
import com.damvih.message.TelegramMessageFactory;
import com.damvih.message.TelegramOutgoingMessage;
import com.damvih.service.CalculationResultService;
import com.damvih.service.MessageDispatcherService;
import com.damvih.util.ResultTextFormatter;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

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
        UserSettingsDto userSettingsDto = (UserSettingsDto) stateContext.getExtendedState().getVariables().get("userSettings");

        String loadingMessage = "Начинается определение результата. Подождите, процесс может занимать до нескольких минут.";
        getMessageDispatcherService().dispatch(
                new TelegramOutgoingMessage(
                        getTelegramMessageFactory().create(userId, loadingMessage)
                )
        );

        List<ParticipantDto> participants = calculationResultService.calculate(userSettingsDto.getGroupId(), userSettingsDto.getAlbumId());
        participants = calculationResultService.sort(participants);
        List<ParticipantDto> winners = calculationResultService.getWinners(participants);

        MessageDispatcherService messageDispatcherService = getMessageDispatcherService();
        TelegramMessageFactory telegramMessageFactory = getTelegramMessageFactory();

        SendMessage sortedParticipantsResultMessage = telegramMessageFactory.create(userId, resultTextFormatter.formatDateTimeNowText() + resultTextFormatter.formatParticipants(participants));
        SendMessage winnersMessage = telegramMessageFactory.create(userId, resultTextFormatter.formatWinners(winners));

        messageDispatcherService.dispatch(new TelegramOutgoingMessage(sortedParticipantsResultMessage));
        messageDispatcherService.dispatch(new TelegramOutgoingMessage(winnersMessage));

        stateContext.getExtendedState().getVariables().put("userId", userId);
    }

}
