package za.co.jse.simplechatplatform.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.jse.simplechatplatform.dto.ChatMessageRequest;
import za.co.jse.simplechatplatform.dto.ChatRoomRequest;
import za.co.jse.simplechatplatform.service.ChatRoomService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatMessageControllerTest {

    @Mock
    private ChatRoomService chatRoomService;

    @InjectMocks
    private ChatMessageController controller;

    private ChatMessageRequest chatMessageRequest;
    private ChatRoomRequest chatRoomRequest;

    @BeforeEach
    void setUp() {
        chatMessageRequest = ChatMessageRequest.builder()
                .roomId("room-1")
                .username("john")
                .content("Hello")
                .build();

        chatRoomRequest = ChatRoomRequest.builder()
                .roomId("room-1")
                .username("john")
                .build();
    }

    @Test
    void shouldDelegateSendMessageToService() {
        controller.sendMessage(chatMessageRequest);
        verify(chatRoomService).sendMessage(chatMessageRequest);
    }

    @Test
    void shouldDelegateLeaveToService() {
        controller.leave(chatRoomRequest);
        verify(chatRoomService).leave(chatRoomRequest);
    }
}