package za.co.jse.simplechatplatform.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import za.co.jse.simplechatplatform.dto.ChatRoomRequest;
import za.co.jse.simplechatplatform.dto.JoinResponse;
import za.co.jse.simplechatplatform.service.ChatRoomService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
}