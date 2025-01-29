package com.damvih.bot;

import com.damvih.bot.handler.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer, SpringLongPollingBot {

    private static final String MESSAGE_FOR_UNKNOWN_HANDLER = "Введена неизвестная команда!";
    private final TelegramClient telegramClient;
    private final HandlerContainer handlerContainer;

    private final String token;

    public TelegramBot(@Value("${TELEGRAM_BOT_TOKEN}") String token, HandlerContainer handlerContainer) {
        this.telegramClient = new OkHttpTelegramClient(token);
        this.handlerContainer = handlerContainer;
        this.token = token;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (hasUpdateTextMessage(update)) {
            String input = update.getMessage().getText().trim().split(" ")[0];

            SendMessage message;
            if (handlerContainer.containsHandler(input)) {
                message = handlerContainer.getHandler(input).prepareMessage(update);
            } else {
                message = getMessageForUnknownHandler(update);
            }

            send(message);
        }
    }

    private SendMessage getMessageForUnknownHandler(Update update) {
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(MESSAGE_FOR_UNKNOWN_HANDLER)
                .build();
    }

    public void send(BotApiMethod<?> request) {
        try {
            telegramClient.execute(request);
        } catch (TelegramApiException exception) {

        }

    }

    private boolean hasUpdateTextMessage(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    @Autowired
    private void initMenu(List<Handler> handlers) {
        List<BotCommand> commands = new ArrayList<>();
        for (Handler handler : handlers) {
            commands.add(new BotCommand(
                    handler.getIdentifier(),
                    handler.getDescription()
            ));
        }
        send(new SetMyCommands(commands));
    }

}
