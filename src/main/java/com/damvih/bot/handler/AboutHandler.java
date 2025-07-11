package com.damvih.bot.handler;

import com.damvih.message.OutgoingMessage;
import com.damvih.message.TelegramOutgoingMessage;
import com.damvih.service.MessageDispatcherService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class AboutHandler extends Handler {

    private static final String MESSAGE = "Правила фотоконкурса: победителем считается пара, чья фотография наберёт наибольшее количество отметок «нравится» в ходе открытого голосования (будут учитываться отметки только в случае, если проголосовавший состоит в группе Юнармия – город Хабаровск.\nИсходный код: [ССЫЛКА]";

    public AboutHandler() {
        super("/about", "о боте");
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

}
