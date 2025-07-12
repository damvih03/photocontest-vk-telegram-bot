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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
@Setter(onMethod_ = {@Autowired})
public class ResultHandler extends Handler {

    private static final int GROUP_ID_MESSAGE_POSITION = 1;
    private static final int ALBUM_ID_MESSAGE_POSITION = 2;
    private static final String DELIMITER = " ";

    private CalculationResultService calculationResultService;
    private MessageDispatcherService messageDispatcherService;

    public ResultHandler() {
        super("/result", "получить результат");
    }

    // TODO: Refactor this method to reduce code
    @Override
    public void perform(Update update) {
        String datetime = getDatetimeNowText();

        String[] messageInput = update.getMessage().getText().split(DELIMITER);

        Long groupId = Long.parseLong(messageInput[GROUP_ID_MESSAGE_POSITION]);
        Long albumId = Long.parseLong(messageInput[ALBUM_ID_MESSAGE_POSITION]);

        SendMessage loadingMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Начинается определение результата. Подождите, процесс может занимать до нескольких минут.")
                .build();
        messageDispatcherService.dispatch(new TelegramOutgoingMessage(loadingMessage));

        List<ParticipantDto> participants = calculationResultService.calculate(groupId, albumId);

        if (participants.isEmpty()) {
            SendMessage emptyMessageError = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Ошибка: альбом не должен быть пустым.")
                    .build();
            messageDispatcherService.dispatch(new TelegramOutgoingMessage(emptyMessageError));
            return;
        }

        participants = calculationResultService.sort(participants);

        List<ParticipantDto> winners = calculationResultService.getWinners(participants);

        SendMessage sortedParticipantResultMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(datetime + createParticipantsSortedText(participants))
                .build();

        SendMessage winnersMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(createWinnersText(winners))
                .build();

        MessageDispatcherService messageDispatcherService = getMessageDispatcherService();
        messageDispatcherService.dispatch(new TelegramOutgoingMessage(sortedParticipantResultMessage));
        messageDispatcherService.dispatch(new TelegramOutgoingMessage(winnersMessage));
    }

    private String createParticipantsSortedText(List<ParticipantDto> participants) {
        StringBuilder text = new StringBuilder();
        Integer lastCounted = null;
        int currentRank = 0;
        int offset = 0;

        text.append("\n*** Результат фотоконкурса 'Двойной удар' ")
                .append(LocalDateTime.now().getYear()).append(" ***\n");

        for (ParticipantDto participant : participants) {
            if (!Objects.equals(participant.getCounted(), lastCounted)) {
                currentRank += offset + 1;
                lastCounted = participant.getCounted();
                offset = 0;
            } else {
                offset += 1;
            }

            text.append(
                    getParticipantOutput(participant, currentRank)
            );
        }
        return text.toString();
    }

    private String getDatetimeNowText() {
        StringBuilder output = new StringBuilder("⏰ Дата и время запроса (по Гринвичу): ");
        ZonedDateTime dateTimeInUTC = ZonedDateTime.now(ZoneOffset.UTC);
        String datetime = dateTimeInUTC.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        return output.append(datetime)
                .append("\n")
                .toString();
    }

    private StringBuilder getParticipantOutput(ParticipantDto participant, int currentRank) {
        return new StringBuilder()
                .append("***").append("\n")
                .append("🏆 Место: ").append(currentRank).append("\n")
                .append("👥 Пара: ").append(participant.getName()).append("\n")
                .append("📊 Счет: ").append(participant.getCounted()).append("/").append(participant.getTotal()).append("\n")
                .append("🆔 ID: ").append(participant.getId()).append("\n")
                .append("***").append("\n");
    }

    private String createWinnersText(List<ParticipantDto> participants) {
        StringBuilder text = new StringBuilder();

        text.append("***").append(" \uD83C\uDFC5 ");
        if (participants.size() > 1) {
            text.append("Победители");
        } else {
            text.append("Победитель");
        }
        text.append(" \uD83C\uDFC5 ");
        text.append("***").append("\n");

        for (ParticipantDto participant : participants) {
            text.append("👥 Пара: ").append(participant.getName()).append("\n");
        }

        return text.toString();
    }

}
