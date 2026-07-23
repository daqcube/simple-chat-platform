package za.co.jse.simplechatplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChatRoomRequest(
        String roomId,
        @NotBlank(message = "Username is required.")
        String username
) {
}
