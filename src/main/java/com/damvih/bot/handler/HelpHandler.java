package com.damvih.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HelpHandler implements Handler {

    private static final String DESCRIPTION = "помощь";
    private String MESSAGE;

    @Override
    public String getIdentifier() {
        return "/help";
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

    @Autowired
    public void initializeDescription(List<Handler> handlers) {
        MESSAGE = handlers.stream()
                .map(handler -> handler.getIdentifier() + " - " + handler.getDescription())
                .collect(Collectors.joining("\n"));

    }

}
