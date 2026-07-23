package za.co.jse.simplechatplatform.dto;

public record ChatRoomResponse(
        String id,
        String name,
        int onlineUsers
) {
}
