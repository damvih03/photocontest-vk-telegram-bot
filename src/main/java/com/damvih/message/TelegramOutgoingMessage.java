package com.damvih.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelegramOutgoingMessage extends OutgoingMessage {

    private BotApiMethod<?> payload;

}
