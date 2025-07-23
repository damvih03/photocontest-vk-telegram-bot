package com.damvih.fsm.action;

import com.damvih.fsm.state.ResultEvent;
import com.damvih.fsm.state.ResultState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class AlbumReceivedResultAction implements Action<ResultState, ResultEvent> {

    @Override
    public void execute(StateContext<ResultState, ResultEvent> stateContext) {
        Update update = (Update) stateContext.getMessageHeader("payload");
        Long albumId = Long.valueOf(update.getMessage().getText());
        stateContext.getExtendedState().getVariables().put("albumId", albumId);
    }

}
