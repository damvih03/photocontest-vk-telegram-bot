package com.damvih.fsm;

import com.damvih.service.InMemoryUserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StateMachineListener<S, E> extends StateMachineListenerAdapter<S, E> {

    private final InMemoryUserStateService inMemoryUserStateService;

    @Override
    public void stateMachineStopped(StateMachine<S, E> stateMachine) {
        Long userId = Long.valueOf(stateMachine.getExtendedState().getVariables().get("userId").toString());
        inMemoryUserStateService.remove(userId);
        stateMachine.stop();
    }

}
