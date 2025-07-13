package com.damvih.bot.handler;

import com.damvih.dto.ParticipantDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
public class ResultTextFormatter {

    public String formatDateTimeNowText() {
        StringBuilder output = new StringBuilder("⏰ Дата и время запроса (по Гринвичу): ");
        ZonedDateTime dateTimeInUTCPlus10 = LocalDateTime.now()
                .atZone(ZoneOffset.UTC);
        String datetime = dateTimeInUTCPlus10.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        return output.append(datetime)
                .append("\n")
                .toString();
    }

    public String formatParticipants(List<ParticipantDto> participants) {
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
                    getParticipant(participant, currentRank)
            );
        }
        return text.toString();
    }

    public String formatWinners(List<ParticipantDto> winners) {
        StringBuilder text = new StringBuilder();

        text.append("***").append(" \uD83C\uDFC5 ");
        if (winners.size() > 1) {
            text.append("Победители");
        } else {
            text.append("Победитель");
        }
        text.append(" \uD83C\uDFC5 ");
        text.append("***").append("\n");

        for (ParticipantDto winner : winners) {
            text.append("👥 Пара: ").append(winner.getName()).append("\n");
        }

        return text.toString();
    }

    private StringBuilder getParticipant(ParticipantDto participant, int currentRank) {
        return new StringBuilder()
                .append("***").append("\n")
                .append("🏆 Место: ").append(currentRank).append("\n")
                .append("👥 Пара: ").append(participant.getName()).append("\n")
                .append("📊 Счет: ").append(participant.getCounted()).append("/").append(participant.getTotal()).append("\n")
                .append("🆔 ID: ").append(participant.getId()).append("\n")
                .append("***").append("\n");
    }

}
