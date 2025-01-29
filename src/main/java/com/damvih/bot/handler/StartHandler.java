package com.damvih.bot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartHandler extends Handler {

    private static final String MESSAGE = "Приветствую, пользователь! Я бот, который позволяет получить результат фотоконкурса 'Двойной удар' города Хабаровска.";

    public StartHandler() {
        super("/start", "приветствие");
    }

    @Override
    public SendMessage  prepareMessage(Update update) {
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(MESSAGE)
                .build();
    }

}
