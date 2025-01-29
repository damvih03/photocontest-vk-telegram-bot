package com.damvih.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HelpHandler extends Handler {

    private String MESSAGE;

    public HelpHandler() {
        super("/help", "помощь");
    }

    @Override
    public SendMessage prepareMessage(Update update) {
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(MESSAGE)
                .build();
    }

    @Autowired
    public void initializeDescription(List<Handler> handlers) {
        MESSAGE = handlers.stream()
                .map(handler -> handler.getIdentifier() + " - " + handler.getDescription())
                .collect(Collectors.joining("\n"));

    }

}
