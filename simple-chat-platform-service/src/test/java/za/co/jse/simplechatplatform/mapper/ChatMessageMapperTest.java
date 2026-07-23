package za.co.jse.simplechatplatform.mapper;

import org.junit.jupiter.api.Test;
import za.co.jse.simplechatplatform.domain.ChatMessage;
import za.co.jse.simplechatplatform.dto.ChatMessageRequest;
import za.co.jse.simplechatplatform.dto.ChatMessageResponse;
import za.co.jse.simplechatplatform.enums.MessageType;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ChatMessageMapperTest {
    private final ChatMessageMapper mapper = new ChatMessageMapper();

    @Test
    void shouldCreateJoinMessage() {
        String username = "john";
        String roomId = "developers";

        ChatMessage message = mapper.toJoinMessage(username, roomId);

        assertNotNull(message);
        assertEquals(username, message.getUsername());
        assertEquals(roomId, message.getRoomId());
        assertEquals(MessageType.JOINED, message.getType());
        assertEquals("john joined the chat", message.getContent());
        assertNotNull(message.getTimestamp());
    }

    @Test
    void shouldMapChatMessageRequestToChatMessage() {
        ChatMessageRequest request = new ChatMessageRequest(
                "developers",
                "john",
                "hello"
        );

        ChatMessage message = mapper.toChatMessage(request);

        assertNotNull(message);
        assertEquals("developers", message.getRoomId());
        assertEquals("john", message.getUsername());
        assertEquals("hello", message.getContent());
        assertEquals(MessageType.CHAT, message.getType());
        assertNotNull(message.getTimestamp());
    }

    @Test
    void shouldCreateLeftMessage() {
        ChatMessage message = mapper.toLeftMessage("john", "developers");
        assertEquals("john", message.getUsername());
        assertEquals("developers", message.getRoomId());
        assertEquals(MessageType.LEFT, message.getType());
        assertEquals("john left the chat", message.getContent());
        assertNotNull(message.getTimestamp());
    }

    @Test
    void shouldMapChatMessageToResponseDto() {
        Instant timestamp = Instant.now();
        ChatMessage message = ChatMessage.builder()
                .username("john")
                .content("hello")
                .timestamp(timestamp)
                .type(MessageType.CHAT)
                .roomId("developers")
                .build();

        ChatMessageResponse response = mapper.toResponseDto(message);

        assertNotNull(response);
        assertEquals("john", response.username());
        assertEquals("hello", response.content());
        assertEquals(timestamp, response.timestamp());
    }

    @Test
    void shouldCreateUniqueTimestampForJoinMessage() {
        ChatMessage first = mapper.toJoinMessage(
                "john",
                "developers"
        );

        ChatMessage second = mapper.toJoinMessage(
                "john",
                "developers"
        );

        assertNotEquals(first.getTimestamp(), second.getTimestamp());

    }
}