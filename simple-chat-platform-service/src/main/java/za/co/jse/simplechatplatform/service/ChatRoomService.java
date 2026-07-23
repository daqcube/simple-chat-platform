package za.co.jse.simplechatplatform.service;

import za.co.jse.simplechatplatform.dto.ChatMessageRequest;
import za.co.jse.simplechatplatform.dto.ChatRoomRequest;
import za.co.jse.simplechatplatform.dto.ChatRoomResponse;
import za.co.jse.simplechatplatform.dto.JoinResponse;

import java.util.Set;

public interface ChatRoomService {

    JoinResponse join(ChatRoomRequest request);

    void sendMessage(ChatMessageRequest request);

    void leave(ChatRoomRequest request);

    Set<ChatRoomResponse> getRooms();

    Set<String> getRoomUsers(String roomId);
}
