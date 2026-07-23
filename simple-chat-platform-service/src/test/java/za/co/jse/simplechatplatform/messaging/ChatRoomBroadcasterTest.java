package za.co.jse.simplechatplatform.messaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import za.co.jse.simplechatplatform.domain.ChatMessage;
import za.co.jse.simplechatplatform.dto.ChatMessageResponse;
import za.co.jse.simplechatplatform.enums.MessageType;
import za.co.jse.simplechatplatform.mapper.ChatMessageMapper;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomBroadcasterTest {

    public static final String MESSAGE_DESTINATION = "/topic/rooms/developers/messages";
    public static final String USERS_DESTINATION = "/topic/rooms/developers/users";
    @Mock
    private ChatMessageMapper chatMessageMapper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatRoomBroadcaster broadcaster;


    @Test
    void shouldBroadcastMessageToRoomTopic() {
        //Given
        String roomId = "developers";

        ChatMessage message = ChatMessage.builder()
                .username("john")
                .content("hello")
                .roomId(roomId)
                .timestamp(Instant.now())
                .build();

        ChatMessageResponse response = new ChatMessageResponse(
                "john",
                "hello",
                message.getTimestamp(),
                MessageType.CHAT
        );

        //When
        when(chatMessageMapper.toResponseDto(message)).thenReturn(response);

        //SUT
        broadcaster.broadcastMessage(roomId, message);

        //Assertions
        verify(chatMessageMapper).toResponseDto(message);
        verify(messagingTemplate).convertAndSend(MESSAGE_DESTINATION, response);
    }


    @Test
    void shouldBroadcastUsersToRoomTopic() {
        //Given
        String roomId = "developers";

        //When
        Set<String> users = Set.of("john", "mary");

        //SUT
        broadcaster.broadcastUsers(roomId, users);

        //Assertions
        verify(messagingTemplate).convertAndSend(USERS_DESTINATION, users);
    }


    @Test
    void shouldCreateCorrectMessageDestination() {
        //Given
        String roomId = "general";

        ChatMessage message = ChatMessage.builder()
                .username("peter")
                .content("hi")
                .build();

        ChatMessageResponse response = new ChatMessageResponse(
                "peter",
                "hi",
                Instant.now(),
                MessageType.CHAT
        );

        //When
        when(chatMessageMapper.toResponseDto(message))
                .thenReturn(response);

        //SUT
        broadcaster.broadcastMessage(roomId, message);

        //Assertions
        ArgumentCaptor<String> destinationCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), eq(response));

        assertEquals("/topic/rooms/general/messages", destinationCaptor.getValue());
    }
}