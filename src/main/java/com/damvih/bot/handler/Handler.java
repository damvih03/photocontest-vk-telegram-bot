package com.damvih.bot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Handler {

    String getIdentifier();
    String getDescription();
    SendMessage prepareMessage(Update update);

}
