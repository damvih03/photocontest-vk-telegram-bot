package com.damvih.bot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

abstract public class Handler {

    private final String identifier;
    private final String description;

    public Handler(String identifier, String description) {
        this.identifier = identifier;
        this.description = capitalize(description);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDescription() {
        return description;
    }

    abstract public SendMessage prepareMessage(Update update);

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

}
