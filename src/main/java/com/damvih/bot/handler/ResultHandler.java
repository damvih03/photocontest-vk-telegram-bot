package com.damvih.bot.handler;

import com.damvih.dto.ParticipantDto;
import com.damvih.service.CalculationResultService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ResultHandler implements Handler {

    private static final int GROUP_ID_MESSAGE_POSITION = 1;
    private static final int ALBUM_ID_MESSAGE_POSITION = 2;
    private static final String DELIMITER = " ";
    public static final int UTC_OFFSET = 10;
    private final CalculationResultService calculationResultService;

    @Override
    public String getIdentifier() {
        return "/result";
    }

    @Override
    public String getDescription() {
        return "получить результат";
    }

    @Override
    public SendMessage prepareMessage(Update update) {
        String datetime = getDatetimeNowText();

        String[] messageInput = update.getMessage().getText().split(DELIMITER);

        Long groupId = Long.parseLong(messageInput[GROUP_ID_MESSAGE_POSITION]);
        Long albumId = Long.parseLong(messageInput[ALBUM_ID_MESSAGE_POSITION]);

        List<ParticipantDto> participants = calculationResultService.calculate(groupId, albumId);
        participants = calculationResultService.sort(participants);

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(
                        datetime + createText(participants))
                .build();
    }

    private String createText(List<ParticipantDto> participants) {
        StringBuilder text = new StringBuilder();
        Integer lastCounted = null;
        int currentRank = 0;
        int offset = 0;

        text.append(getHeaderOutput());

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
        StringBuilder output = new StringBuilder("⏰ Дата и время запроса (+" + UTC_OFFSET + " UTC): ");
        ZonedDateTime dateTimeInUTCPlus10 = LocalDateTime.now()
                .atZone(ZoneOffset.ofHours(UTC_OFFSET));
        String datetime = dateTimeInUTCPlus10.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        return output.append(datetime)
                .append("\n")
                .toString();
    }

    private String getHeaderOutput() {
        return "\n*** Результат фотоконкурса 'Двойной удар' " +
                LocalDateTime.now().getYear() +
                " ***\n";
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
}
