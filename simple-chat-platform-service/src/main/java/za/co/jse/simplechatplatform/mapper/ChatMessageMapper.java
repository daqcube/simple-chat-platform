package za.co.jse.simplechatplatform.mapper;

import org.springframework.stereotype.Component;
import za.co.jse.simplechatplatform.domain.ChatMessage;
import za.co.jse.simplechatplatform.dto.ChatMessageRequest;
import za.co.jse.simplechatplatform.dto.ChatMessageResponse;
import za.co.jse.simplechatplatform.enums.MessageType;

import java.time.Instant;

@Component
public class ChatMessageMapper {

    public ChatMessage toLeftMessage(String username, String roomId) {
        return ChatMessage.builder()
                .username(username)
                .type(MessageType.LEFT)
                .roomId(roomId)
                .content(username + " left the chat")
                .timestamp(Instant.now())
                .build();
    }

    public ChatMessage toJoinMessage(String username, String roomId) {
        return ChatMessage.builder()
                .username(username)
                .type(MessageType.JOINED)
                .roomId(roomId)
                .content(username + " joined the chat")
                .timestamp(Instant.now())
                .build();
    }

    public ChatMessage toChatMessage(ChatMessageRequest request) {
        return ChatMessage.builder()
                .roomId(request.roomId())
                .username(request.username())
                .content(request.content())
                .type(MessageType.CHAT)
                .timestamp(Instant.now())
                .build();
    }

    public ChatMessageResponse toResponseDto(ChatMessage message) {
        return new ChatMessageResponse(
                message.getUsername(),
                message.getContent(),
                message.getTimestamp(),
                message.getType()
        );
    }
}
