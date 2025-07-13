package com.damvih.bot.handler;

import com.damvih.dto.ParticipantDto;
import com.damvih.message.TelegramOutgoingMessage;
import com.damvih.service.CalculationResultService;
import com.damvih.service.MessageDispatcherService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@Setter(onMethod_ = {@Autowired})
public class ResultHandler extends Handler {

    private static final int GROUP_ID_MESSAGE_POSITION = 1;
    private static final int ALBUM_ID_MESSAGE_POSITION = 2;
    private static final String DELIMITER = " ";

    private static final String LOADING_MESSAGE = "Начинается определение результата. Подождите, процесс может занимать до нескольких минут.";
    private static final String EMPTY_ALBUM_MESSAGE = "Ошибка: альбом не должен быть пустым.";

    private CalculationResultService calculationResultService;
    private MessageDispatcherService messageDispatcherService;
    private ResultTextFormatter resultTextFormatter;

    public ResultHandler() {
        super("/result", "получить результат");
    }

    // TODO: Refactor this method to reduce code
    @Override
    public void perform(Update update) {
        String datetime = resultTextFormatter.formatDateTimeNowText();

        String[] messageInput = update.getMessage().getText().split(DELIMITER);

        Long groupId = Long.parseLong(messageInput[GROUP_ID_MESSAGE_POSITION]);
        Long albumId = Long.parseLong(messageInput[ALBUM_ID_MESSAGE_POSITION]);

        SendMessage loadingMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(LOADING_MESSAGE)
                .build();
        messageDispatcherService.dispatch(new TelegramOutgoingMessage(loadingMessage));

        List<ParticipantDto> participants = calculationResultService.calculate(groupId, albumId);

        if (participants.isEmpty()) {
            SendMessage emptyMessageError = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text(EMPTY_ALBUM_MESSAGE)
                    .build();
            messageDispatcherService.dispatch(new TelegramOutgoingMessage(emptyMessageError));
            return;
        }

        participants = calculationResultService.sort(participants);

        List<ParticipantDto> winners = calculationResultService.getWinners(participants);

        SendMessage sortedParticipantResultMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(datetime + resultTextFormatter.formatParticipants(participants))
                .build();

        SendMessage winnersMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(resultTextFormatter.formatWinners(winners))
                .build();

        MessageDispatcherService messageDispatcherService = getMessageDispatcherService();
        messageDispatcherService.dispatch(new TelegramOutgoingMessage(sortedParticipantResultMessage));
        messageDispatcherService.dispatch(new TelegramOutgoingMessage(winnersMessage));
    }

}
