package com.damvih.fsm;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class ResultStateMachineConfig extends EnumStateMachineConfigurerAdapter<ResultState, ResultEvent> {

    private final GroupReceivedResultAction groupReceivedResultAction;
    private final AlbumReceivedResultAction albumReceivedResultAction;
    private final ConfirmationReceivedResultAction confirmationReceivedResultAction;
    private final StateMachineListener<ResultState, ResultEvent> stateMachineListener;

    @Override
    public void configure(StateMachineConfigurationConfigurer<ResultState, ResultEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true)
                .listener(stateMachineListener);
    }

    @Override
    public void configure(StateMachineStateConfigurer<ResultState, ResultEvent> states) throws Exception {
        states.withStates()
                .initial(ResultState.WAITING_FOR_GROUP)
                .state(ResultState.WAITING_FOR_ALBUM)
                .state(ResultState.WAITING_FOR_CONFIRMATION)
                .end(ResultState.COMPLETED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ResultState, ResultEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(ResultState.WAITING_FOR_GROUP)
                    .target(ResultState.WAITING_FOR_ALBUM)
                    .event(ResultEvent.GROUP_RECEIVED)
                    .action(groupReceivedResultAction)
                .and()
                .withExternal()
                    .source(ResultState.WAITING_FOR_ALBUM)
                    .target(ResultState.WAITING_FOR_CONFIRMATION)
                    .event(ResultEvent.ALBUM_RECEIVED)
                    .action(albumReceivedResultAction)
                .and()
                .withExternal()
                    .source(ResultState.WAITING_FOR_CONFIRMATION)
                    .target(ResultState.COMPLETED)
                    .event(ResultEvent.CONFIRMATION_RECEIVED)
                    .action(confirmationReceivedResultAction);
    }

}
