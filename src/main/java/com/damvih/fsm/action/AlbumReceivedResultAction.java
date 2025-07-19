package com.damvih.fsm.action;

import com.damvih.dto.UserSettingsDto;
import com.damvih.fsm.state.ResultEvent;
import com.damvih.fsm.state.ResultState;
import com.damvih.message.TelegramMessageFactory;
import com.damvih.message.TelegramOutgoingMessage;
import com.damvih.service.MessageDispatcherService;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class AlbumReceivedResultAction extends ResultAction {

    public AlbumReceivedResultAction(MessageDispatcherService messageDispatcherService, TelegramMessageFactory telegramMessageFactory) {
        super(messageDispatcherService, telegramMessageFactory);
    }

    @Override
    public void execute(StateContext<ResultState, ResultEvent> stateContext) {
        Update update = (Update) stateContext.getMessageHeader("payload");

        Long userId = update.getMessage().getChatId();
        Long groupId = Long.valueOf(stateContext.getExtendedState().getVariables().get("groupId").toString());
        Long albumId = Long.valueOf(update.getMessage().getText());

        UserSettingsDto userSettingsDto = new UserSettingsDto(groupId, albumId);
        String message = """
            Вы ввели:
            Группа: %s
            Альбом: %s
            
            Подтвердите отправку результата — введите "да" или "нет"
            """.formatted(userSettingsDto.getGroupId(), userSettingsDto.getAlbumId());
        getMessageDispatcherService().dispatch(
                new TelegramOutgoingMessage(
                        getTelegramMessageFactory().create(userId, message)
                )
        );
        stateContext.getExtendedState().getVariables().put("userSettings", userSettingsDto);
    }

}
