package za.co.jse.simplechatplatform.messaging;

import za.co.jse.simplechatplatform.domain.ChatMessage;

public interface ChatMessageQueue {

    void publish(ChatMessage message);

    ChatMessage consume() throws InterruptedException;
}
