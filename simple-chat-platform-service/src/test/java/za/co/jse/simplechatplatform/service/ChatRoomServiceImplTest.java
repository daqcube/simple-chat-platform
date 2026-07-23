package za.co.jse.simplechatplatform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import za.co.jse.simplechatplatform.domain.ChatMessage;
import za.co.jse.simplechatplatform.dto.ChatMessageRequest;
import za.co.jse.simplechatplatform.dto.ChatRoomRequest;
import za.co.jse.simplechatplatform.dto.ChatRoomResponse;
import za.co.jse.simplechatplatform.dto.JoinResponse;
import za.co.jse.simplechatplatform.mapper.ChatMessageMapper;
import za.co.jse.simplechatplatform.messaging.ChatMessageProducer;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceImplTest {
    public static final String GENERAL_ROOM_ID = "general";
    public static final java.lang.String DEVELOPERS_ROOM_ID = "developers";
    @Mock
    private ChatMessageProducer producer;

    @Mock
    private ChatMessageMapper mapper;

    @InjectMocks
    ChatRoomServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "defaultRoom", GENERAL_ROOM_ID);
    }

    @Test
    void shouldJoinDefaultRoomWhenRoomIsNull() {
        String username = "john";
        String roomId = GENERAL_ROOM_ID;
        ChatRoomRequest request = new ChatRoomRequest(null, username);
        ChatMessage message = buildMessage(null, username);

        when(mapper.toJoinMessage(username, roomId)).thenReturn(message);

        JoinResponse response = service.join(request);
        assertEquals(roomId, response.roomId());

        verify(mapper).toJoinMessage(username, roomId);
        verify(producer).publish(message);

        assertTrue(service.getRooms()
                .stream()
                .anyMatch(room -> room.roomId().equals(roomId)));
    }


    @Test
    void shouldJoinDefaultRoomWhenRoomIsBlank() {
        //Given
        String username = "john";
        String roomId = "";
        ChatRoomRequest request = new ChatRoomRequest(roomId, username);
        ChatMessage message = buildMessage( username, roomId);
        //When
        when(mapper.toJoinMessage(username, GENERAL_ROOM_ID)).thenReturn(message);

        //SUT
        JoinResponse response = service.join(request);

        //Asserts
        assertEquals(GENERAL_ROOM_ID, response.roomId());
        verify(producer).publish(message);
    }

    @Test
    void shouldJoinSpecifiedRoom() {
        //Given
        String roomId = DEVELOPERS_ROOM_ID;
        String username = "john";
        ChatRoomRequest request = new ChatRoomRequest(roomId, username);
        ChatMessage message = buildMessage(username, roomId);

        //When
        when(mapper.toJoinMessage(username, roomId)).thenReturn(message);

        //SUT
        JoinResponse response = service.join(request);

        //Assert
        assertEquals(username, response.username());
        assertEquals(roomId, response.roomId());
        assertNotNull(response.joinedAt());

        verify(mapper).toJoinMessage(username, roomId);
        verify(producer).publish(message);
        assertTrue(service.getRooms()
                .stream()
                .anyMatch(room -> room.roomId().equals(roomId)));
    }

    @Test
    void shouldReturnEmptyRoomsWhenNoRoomsExist() {
        Set<ChatRoomResponse> rooms = service.getRooms();
        assertNotNull(rooms);
        assertTrue(rooms.isEmpty());
    }

    @Test
    void shouldReturnRoomsWithUsers() {
        //Given
        ChatRoomRequest john = new ChatRoomRequest(DEVELOPERS_ROOM_ID, "john");
        ChatRoomRequest mary = new ChatRoomRequest(DEVELOPERS_ROOM_ID, "mary");
        ChatRoomRequest peter = new ChatRoomRequest(GENERAL_ROOM_ID, "peter");

        //When
        when(mapper.toJoinMessage(anyString(), anyString()))
                .thenReturn(new ChatMessage());

        service.join(john);
        service.join(mary);
        service.join(peter);

        //SUT
        Set<ChatRoomResponse> rooms = service.getRooms();

        assertNotNull(rooms);
        assertEquals(2, rooms.size());

        ChatRoomResponse developersRoom = rooms.stream()
                .filter(room -> room.roomId().equals(DEVELOPERS_ROOM_ID))
                .findFirst()
                .orElseThrow();

        assertEquals(Set.of("john", "mary"), developersRoom.users());

        ChatRoomResponse generalRoom = rooms.stream()
                .filter(room -> room.roomId().equals(GENERAL_ROOM_ID))
                .findFirst()
                .orElseThrow();

        assertEquals(Set.of("peter"), generalRoom.users());
    }

    @Test
    void shouldSendMessage() {
        //Given
        String roomId = DEVELOPERS_ROOM_ID;
        String username = "john";
        ChatMessageRequest request = new ChatMessageRequest(roomId, username, "Hello");
        ChatMessage message = buildMessage(username, roomId);

        //When
        when(mapper.toChatMessage(request)).thenReturn(message);

        //SUT
        service.sendMessage(request);

        //Verify
        verify(mapper).toChatMessage(request);
        verify(producer).publish(message);
    }

    @Test
    void shouldDoNothingWhenLeavingNonExistingRoom() {
        //Given
        ChatRoomRequest request = new ChatRoomRequest(
                "unknown-room",
                "john"
        );
        //SUT
        service.leave(request);

        //Assertions
        verifyNoInteractions(mapper);
        verifyNoInteractions(producer);
    }

    @Test
    void shouldRemoveUserAndPublishLeftMessageWhenLeavingRoom() {
        //Given
        String roomId = DEVELOPERS_ROOM_ID;
        String username = "john";

        ChatMessage johnLeftTheChat = ChatMessage.builder()
                .username(username)
                .roomId(roomId)
                .content("john left the chat")
                .build();

        //When
        when(mapper.toLeftMessage(username, roomId)).thenReturn(johnLeftTheChat);
        ChatRoomRequest request = new ChatRoomRequest(roomId, username);
        service.join(request);

        //SUT
        service.leave(request);
        Set<String> users = service.getRoomUsers(roomId);

        //Assertions
        assertFalse(users.contains(username));
        verify(mapper).toLeftMessage(username, roomId);
        verify(producer).publish(any(ChatMessage.class));
    }

    @Test
    void shouldReturnUsersForExistingRoom() {
        String roomId = DEVELOPERS_ROOM_ID;

        service.join(new ChatRoomRequest(roomId, "john"));
        service.join(new ChatRoomRequest(roomId, "mary"));

        Set<String> users = service.getRoomUsers(roomId);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.contains("john"));
        assertTrue(users.contains("mary"));
    }

    @Test
    void shouldReturnEmptySetWhenRoomDoesNotExist() {
        Set<String> users = service.getRoomUsers("unknown-room");
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    private ChatMessage buildMessage(String username, String roomId) {
        return ChatMessage.builder()
                .roomId(roomId)
                .username(username)
                .build();
    }
}