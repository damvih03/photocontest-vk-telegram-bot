package com.damvih.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class FSMService {

    private final StateMachineFactory<?, ?> stateMachineFactory;
    private final InMemoryUserStateService inMemoryUserStateService;

    public StateMachine<?, ?> create(Long id, String identifier) {
        StateMachine<?, ?> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put("identifier", identifier);
        stateMachine.getExtendedState().getVariables().put("userId", id);
        inMemoryUserStateService.add(id, stateMachine);
        return stateMachine;
    }

    public <E extends Enum<E>> boolean sendEvent(Long id, E event, Update update) {
        StateMachine<?, E> stateMachine = (StateMachine<?, E>) inMemoryUserStateService.getStateByUserId(id);

        Message<?> message = MessageBuilder.withPayload(event)
                .setHeader("payload", update)
                .build();
        return stateMachine.sendEvent((Message<E>) message);
    }

    public Object getState(Long id) {
        StateMachine<?, ?> stateMachine = inMemoryUserStateService.getStateByUserId(id);
        return stateMachine.getState().getId();
    }

    public String getHandlerIdentifier(Long id) {
        StateMachine<?, ?> stateMachine = inMemoryUserStateService.getStateByUserId(id);
        Object identifier = stateMachine.getExtendedState().getVariables().get("identifier");
        if (identifier == null) {
            throw new IllegalStateException("FSM exists but handler identifier is missing.");
        }
        return identifier.toString();
    }

    public boolean hasMachine(Long id) {
        return inMemoryUserStateService.hasMachine(id);
    }

}
