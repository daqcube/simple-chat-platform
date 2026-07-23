package za.co.jse.simplechatplatform.messaging;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import za.co.jse.simplechatplatform.domain.ChatMessage;
import za.co.jse.simplechatplatform.mapper.ChatMessageMapper;

import java.util.Set;

@Component
public class ChatRoomBroadcaster {
    private final ChatMessageMapper chatMessageMapper;

    private final SimpMessagingTemplate messagingTemplate;

    public ChatRoomBroadcaster(ChatMessageMapper chatMessageMapper, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageMapper = chatMessageMapper;
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastMessage(String roomId, ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/messages", chatMessageMapper.toResponseDto(message));
    }

    public void broadcastUsers(String roomId, Set<String> users) {
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/users", users);
    }
}
