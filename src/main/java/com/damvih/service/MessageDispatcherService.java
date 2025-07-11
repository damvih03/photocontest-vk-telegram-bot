package com.damvih.service;

import com.damvih.exception.UnsupportedMessageTypeException;
import com.damvih.message.OutgoingMessage;
import com.damvih.service.sender.MessageSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageDispatcherService {

    private final List<MessageSenderService<? extends OutgoingMessage>> senders;

    public void dispatch(OutgoingMessage message) {
        for (MessageSenderService<? extends OutgoingMessage> sender : senders) {
            if (sender.canSend(message)) {
                sender.send(message);
                return;
            }
        }
        throw new UnsupportedMessageTypeException("Unsupported message type: " + message.getClass().getName());
    }

}
