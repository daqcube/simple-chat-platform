package za.co.jse.simplechatplatform.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.jse.simplechatplatform.dto.ChatRoomRequest;
import za.co.jse.simplechatplatform.dto.ChatRoomResponse;
import za.co.jse.simplechatplatform.dto.JoinResponse;
import za.co.jse.simplechatplatform.service.ChatRoomService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomControllerTest {

    @Mock
    private ChatRoomService chatRoomService;

    @InjectMocks
    private ChatRoomController controller;

    private ChatRoomRequest request;
    private JoinResponse response;

    @BeforeEach
    void setUp() {
        request = ChatRoomRequest.builder()
                .roomId("room-1")
                .username("john")
                .build();

        response = JoinResponse.builder()
                .roomId("room-1")
                .username("john")
                .build();
    }

    @Test
    void shouldJoinChatRoom() {
        when(chatRoomService.join(request)).thenReturn(response);

        ResponseEntity<JoinResponse> result = controller.join(request);

        assertEquals(200, result.getStatusCode().value());
        assertSame(response, result.getBody());

        verify(chatRoomService).join(request);
    }

    @Test
    void shouldReturnChatRooms() {
        // Given
        Set<ChatRoomResponse> rooms = Set.of(
                new ChatRoomResponse("general", "General", 2),
                new ChatRoomResponse("java", "Java Developers", 1)
        );

        when(chatRoomService.getRooms()).thenReturn(rooms);

        // When
        ResponseEntity<Set<ChatRoomResponse>> roomResponse = controller.getRooms();

        // Then
        assertNotNull(roomResponse);
        assertEquals(HttpStatus.OK, roomResponse.getStatusCode());
        assertEquals(rooms, roomResponse.getBody());

        verify(chatRoomService).getRooms();
    }
}