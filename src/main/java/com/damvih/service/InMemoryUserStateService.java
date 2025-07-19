package com.damvih.service;

import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class InMemoryUserStateService {

    private final ConcurrentHashMap<Long, StateMachine<?, ?>> userMachines = new ConcurrentHashMap<>();

    public StateMachine<?, ?> getStateByUserId(Long userId) {
        return userMachines.get(userId);
    }

    public void remove(Long userId) {
        userMachines.remove(userId);
    }

    public void add(Long userId, StateMachine<?, ?> stateMachine) {
        userMachines.put(userId, stateMachine);
    }

    public boolean hasMachine(Long userId) {
        return userMachines.containsKey(userId);
    }

}
