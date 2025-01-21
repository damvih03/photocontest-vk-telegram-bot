package com.damvih.bot.handler;

import com.damvih.dto.ParticipantDto;
import com.damvih.service.CalculationResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResultHandler implements Handler {

    private static final int GROUP_ID_MESSAGE_POSITION = 1;
    private static final int ALBUM_ID_MESSAGE_POSITION = 2;
    private static final String DELIMITER = " ";
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

        String[] messageInput = update.getMessage().getText().split(DELIMITER);

        Long groupId = Long.parseLong(messageInput[GROUP_ID_MESSAGE_POSITION]);
        Long albumId = Long.parseLong(messageInput[ALBUM_ID_MESSAGE_POSITION]);

        List<ParticipantDto> participants = calculationResultService.calculate(groupId, albumId);
        participants = calculationResultService.sort(participants);

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(createText(participants))
                .build();
    }

    private String createText(List<ParticipantDto> participants) {
        String text = "";
        for (ParticipantDto participant : participants) {
            text += "ID: " + participant.getId() + " | " + participant.getCounted() + "/" + participant.getTotal() + "\n";
        }
        return text;
    }
}
