package com.damvih.bot.handler;

import com.damvih.service.MessageDispatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;

abstract public class Handler {

    private final String identifier;
    private final String description;

    @Autowired
    private MessageDispatcherService messageDispatcherService;

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

    protected MessageDispatcherService getMessageDispatcherService() {
        return messageDispatcherService;
    }

    abstract public void perform(Update update);

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

}
