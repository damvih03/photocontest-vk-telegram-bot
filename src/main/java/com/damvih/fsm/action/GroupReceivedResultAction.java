package com.damvih.fsm.action;

import com.damvih.fsm.state.ResultEvent;
import com.damvih.fsm.state.ResultState;
import com.damvih.message.TelegramMessageFactory;
import com.damvih.message.TelegramOutgoingMessage;
import com.damvih.service.MessageDispatcherService;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class GroupReceivedResultAction extends ResultAction {

    public GroupReceivedResultAction(MessageDispatcherService messageDispatcherService, TelegramMessageFactory telegramMessageFactory) {
        super(messageDispatcherService, telegramMessageFactory);
    }

    @Override
    public void execute(StateContext<ResultState, ResultEvent> stateContext) {
        Update update = (Update) stateContext.getMessageHeader("payload");

        Long userId = update.getMessage().getChatId();
        String input = update.getMessage().getText();

        stateContext.getExtendedState().getVariables().put("groupId", input);
        getMessageDispatcherService().dispatch(
                new TelegramOutgoingMessage(
                        getTelegramMessageFactory().create(userId, "Введите номер альбома: ")
                ));
    }

}
