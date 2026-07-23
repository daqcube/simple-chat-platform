package za.co.jse.simplechatplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChatMessageRequest(
        @NotBlank(message = "Room Id is required.")
        String roomId,
        @NotBlank(message = "Username is required.")
        String username,
        @NotBlank(message = "Message content is required")
        String content) {
}
