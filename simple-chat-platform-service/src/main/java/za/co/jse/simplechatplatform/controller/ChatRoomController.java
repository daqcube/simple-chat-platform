package za.co.jse.simplechatplatform.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.jse.simplechatplatform.dto.ChatRoomRequest;
import za.co.jse.simplechatplatform.dto.ChatRoomResponse;
import za.co.jse.simplechatplatform.dto.JoinResponse;
import za.co.jse.simplechatplatform.service.ChatRoomService;

import java.util.Set;

@RestController
@RequestMapping("/api/chat")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @PostMapping("/join")
    public ResponseEntity<JoinResponse> join(@Valid @RequestBody ChatRoomRequest request) {
        JoinResponse response = chatRoomService.join(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rooms")
    public ResponseEntity<Set<ChatRoomResponse>> getRooms() {
        return ResponseEntity.ok(chatRoomService.getRooms());
    }
}