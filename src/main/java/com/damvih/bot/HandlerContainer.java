package com.damvih.bot;

import com.damvih.bot.handler.Handler;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class HandlerContainer {

    private final Map<String, Handler> handlers;

    public HandlerContainer(List<Handler> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(
                        Handler::getIdentifier, Function.identity()
                ));
    }

    public Handler getHandler(String identifier) {
        Handler handler = handlers.get(identifier);
        if (handler == null) {
            throw new NoSuchElementException(identifier);
        }
        return handler;
    }

    public boolean containsHandler(String identifier) {
        return handlers.containsKey(identifier);
    }

}
