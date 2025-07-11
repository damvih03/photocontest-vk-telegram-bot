package com.damvih.service.sender;

import com.damvih.message.TelegramOutgoingMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TelegramMessageSenderService extends MessageSenderService<TelegramOutgoingMessage> {

    private final TelegramClient telegramClient;

    public TelegramMessageSenderService(@Value("${TELEGRAM_BOT_TOKEN}") String token) {
        super(TelegramOutgoingMessage.class);
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    protected void perform(TelegramOutgoingMessage message) {
        try {
            telegramClient.execute(message.getPayload());
        } catch (TelegramApiException exception) {

        }
    }

}
