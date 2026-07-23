package za.co.jse.simplechatplatform.dto;

import java.util.Set;

public record ChatRoomResponse(
        String roomId,
        Set<String> users
) {
}
