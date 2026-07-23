package za.co.jse.simplechatplatform.messaging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import za.co.jse.simplechatplatform.domain.ChatMessage;
import za.co.jse.simplechatplatform.enums.MessageType;
import za.co.jse.simplechatplatform.service.ChatRoomService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class ChatMessageConsumerTest {
    public static final String DEVELOPERS_ROOM_ID = "developers";
    private final ChatMessageQueue queue = mock(ChatMessageQueue.class);
    private final ChatRoomBroadcaster broadcaster = mock(ChatRoomBroadcaster.class);
    private final ChatRoomService chatRoomService = mock(ChatRoomService.class);
    private ChatMessageConsumer consumer;

    @AfterEach
    void cleanup() {
        if (consumer != null) {
            consumer.stop();
        }
    }

    @Test
    void shouldBroadcastChatMessage() throws Exception {
        ChatMessage message = ChatMessage.builder()
                .roomId(DEVELOPERS_ROOM_ID)
                .username("john")
                .content("hello")
                .type(MessageType.CHAT)
                .build();

        when(queue.consume()).thenReturn(message)
                .thenThrow(new InterruptedException());

        consumer = new ChatMessageConsumer(
                queue,
                broadcaster,
                chatRoomService
        );

        consumer.start();

        Thread.sleep(100);

        verify(broadcaster).broadcastMessage(
                DEVELOPERS_ROOM_ID,
                message
        );

        verify(broadcaster, never())
                .broadcastUsers(any(), any());

    }


    @Test
    void shouldBroadcastJoinedMessageAndUsers() throws Exception {
        ChatMessage message = ChatMessage.builder()
                .roomId(DEVELOPERS_ROOM_ID)
                .username("john")
                .content("john joined")
                .type(MessageType.JOINED)
                .build();

        when(queue.consume())
                .thenReturn(message)
                .thenThrow(new InterruptedException());

        when(chatRoomService.getRoomUsers(DEVELOPERS_ROOM_ID))
                .thenReturn(Set.of("john"));

        consumer = new ChatMessageConsumer(
                queue,
                broadcaster,
                chatRoomService
        );

        consumer.start();

        Thread.sleep(100);

        verify(broadcaster).broadcastMessage(
                DEVELOPERS_ROOM_ID,
                message
        );

        verify(broadcaster).broadcastUsers(
                DEVELOPERS_ROOM_ID,
                Set.of("john")
        );
    }

    @Test
    void shouldBroadcastLeftMessageAndUsers() throws Exception {
        ChatMessage message = ChatMessage.builder()
                .roomId(DEVELOPERS_ROOM_ID)
                .username("john")
                .content("john left")
                .type(MessageType.LEFT)
                .build();

        when(queue.consume())
                .thenReturn(message)
                .thenThrow(new InterruptedException());

        when(chatRoomService.getRoomUsers(DEVELOPERS_ROOM_ID))
                .thenReturn(Set.of());

        consumer = new ChatMessageConsumer(
                queue,
                broadcaster,
                chatRoomService
        );


        consumer.start();

        Thread.sleep(100);


        verify(broadcaster).broadcastMessage(
                DEVELOPERS_ROOM_ID,
                message
        );

        verify(broadcaster).broadcastUsers(
                DEVELOPERS_ROOM_ID,
                Set.of()
        );
    }


    @Test
    void shouldStopWhenConsumerIsInterrupted() throws Exception {
        when(queue.consume()).thenThrow(new InterruptedException());

        consumer = new ChatMessageConsumer(
                queue,
                broadcaster,
                chatRoomService
        );

        consumer.start();

        Thread.sleep(100);

        verify(queue).consume();
    }

    @Test
    void shouldShutdownExecutorOnStop() {
        consumer = new ChatMessageConsumer(
                queue,
                broadcaster,
                chatRoomService
        );

        assertDoesNotThrow(() -> consumer.stop());
    }
}