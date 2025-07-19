package com.damvih.fsm.action;

import com.damvih.fsm.state.ResultEvent;
import com.damvih.fsm.state.ResultState;
import com.damvih.message.TelegramMessageFactory;
import com.damvih.service.MessageDispatcherService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.action.Action;

@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
abstract public class ResultAction implements Action<ResultState, ResultEvent> {

    private final MessageDispatcherService messageDispatcherService;
    private final TelegramMessageFactory telegramMessageFactory;

}
