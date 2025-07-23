package com.damvih.fsm.action;

import com.damvih.message.TelegramMessageFactory;
import com.damvih.message.TelegramOutgoingMessage;
import com.damvih.service.MessageDispatcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@Slf4j
@RequiredArgsConstructor
public class ActionFactory {

    private final MessageDispatcherService messageDispatcherService;
    private final TelegramMessageFactory telegramMessageFactory;

    public <S, E> Action<S, E> create(String message) {
        return stateContext -> {
            Long userId = stateContext.getExtendedState().get("userId", Long.class);
            SendMessage sendMessage = telegramMessageFactory.create(userId, message);
            messageDispatcherService.dispatch(new TelegramOutgoingMessage(sendMessage));
        };
    }

    public <S, E> Action<S, E> createConfirmationMessage() {
        return stateContext -> {
            Long userId = stateContext.getExtendedState().get("userId", Long.class);
            Long groupId = stateContext.getExtendedState().get("groupId", Long.class);
            Long albumId = stateContext.getExtendedState().get("albumId", Long.class);
            String message = """
                    Введенные данные:
                    группа - %s
                    альбом - %s
                    
                    Подтвердите отправку результата — введите "Да" или "Нет"
                    """.formatted(groupId, albumId);

            SendMessage sendMessage = telegramMessageFactory.create(userId, message);
            messageDispatcherService.dispatch(new TelegramOutgoingMessage(sendMessage));
        };
    }

}
