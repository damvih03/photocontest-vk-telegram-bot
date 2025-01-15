package com.damvih.bot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class AboutHandler implements Handler {

    private static final String MESSAGE = "Правила фотоконкурса: победителем считается пара, чья фотография наберёт наибольшее количество отметок «нравится» в ходе открытого голосования (будут учитываться отметки только в случае, если проголосовавший состоит в группе Юнармия – город Хабаровск.\nИсходный код: [ССЫЛКА]";
    private static final String DESCRIPTION = "о боте";

    @Override
    public String getIdentifier() {
        return "/about";
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public SendMessage prepareMessage(Update update) {
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(MESSAGE)
                .build();
    }

}
