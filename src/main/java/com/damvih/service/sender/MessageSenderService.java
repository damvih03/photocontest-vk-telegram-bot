package com.damvih.service.sender;

import com.damvih.message.OutgoingMessage;

abstract public class MessageSenderService<T extends OutgoingMessage> {

    public MessageSenderService(Class<T> type) {
        this.type = type;
    }

    private final Class<T> type;

    public boolean canSend(OutgoingMessage message) {
        return type.isAssignableFrom(message.getClass());
    }

    public void send(OutgoingMessage message) {
        perform(type.cast(message));
    }

    abstract protected void perform(T message);

}
