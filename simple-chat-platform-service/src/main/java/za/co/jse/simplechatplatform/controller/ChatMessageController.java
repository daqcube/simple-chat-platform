package za.co.jse.simplechatplatform.controller;

import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import za.co.jse.simplechatplatform.dto.ChatMessageRequest;
import za.co.jse.simplechatplatform.dto.ChatRoomRequest;
import za.co.jse.simplechatplatform.service.ChatRoomService;

@Controller
@Validated
public class ChatMessageController {
    private final ChatRoomService chatRoomService;

    public ChatMessageController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Valid ChatMessageRequest request) {
        chatRoomService.sendMessage(request);
    }

    @MessageMapping("/chat.leave")
    public void leave(@Valid ChatRoomRequest request) {
        chatRoomService.leave(request);
    }
}