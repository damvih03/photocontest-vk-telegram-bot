package com.damvih.bot.handler;

import com.damvih.fsm.state.ResultEvent;
import com.damvih.fsm.state.ResultState;
import com.damvih.message.TelegramMessageFactory;
import com.damvih.message.TelegramOutgoingMessage;
import com.damvih.service.FSMService;
import com.damvih.service.MessageDispatcherService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Setter(onMethod_ = {@Autowired})
public class ResultHandler extends Handler {

    private TelegramMessageFactory telegramMessageFactory;

    private FSMService fsmService;

    public ResultHandler() {
        super("/result", "получить результат");
    }

    @Override
    public void perform(Update update) {
        Long userId = update.getMessage().getChatId();
        String input = update.getMessage().getText();

        MessageDispatcherService messageDispatcherService = getMessageDispatcherService();

        if (!fsmService.hasMachine(userId)) {
            fsmService.create(userId, getIdentifier());
            SendMessage awaitingGroupMessage = telegramMessageFactory.create(userId, "Введите номер группы: ");
            messageDispatcherService.dispatch(new TelegramOutgoingMessage(awaitingGroupMessage));
            return;
        }
        ResultState resultState = (ResultState) fsmService.getState(userId);
        ResultEvent event = mapInputToEvent(resultState, input);
        fsmService.sendEvent(userId, event, update);
    }

    private ResultEvent mapInputToEvent(ResultState state, String input) {
        return switch (state) {
            case WAITING_FOR_GROUP -> ResultEvent.GROUP_RECEIVED;
            case WAITING_FOR_ALBUM -> ResultEvent.ALBUM_RECEIVED;
            case WAITING_FOR_CONFIRMATION -> {
                if (input.equalsIgnoreCase("да")) {
                    yield ResultEvent.CONFIRMATION_RECEIVED;
                } else if (input.equalsIgnoreCase("нет")) {
                    yield ResultEvent.CANCEL_RECEIVED;
                } else {
                    throw new IllegalArgumentException("Неверный ввод на этапе подтверждения");
                }
            }
            default -> throw new IllegalStateException("Неизвестное состояние: " + state);
        };
    }

}
