package com.damvih.bot.handler;

import com.damvih.message.OutgoingMessage;
import com.damvih.message.TelegramOutgoingMessage;
import com.damvih.service.MessageDispatcherService;
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
    public void perform(Update update) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(MESSAGE)
                .build();

        OutgoingMessage message = new TelegramOutgoingMessage(sendMessage);
        MessageDispatcherService messageDispatcherService = getMessageDispatcherService();
        messageDispatcherService.dispatch(message);
    }

    @Autowired
    public void initializeDescription(List<Handler> handlers) {
        MESSAGE = handlers.stream()
                .map(handler -> handler.getIdentifier() + " - " + handler.getDescription())
                .collect(Collectors.joining("\n"));

    }

}
