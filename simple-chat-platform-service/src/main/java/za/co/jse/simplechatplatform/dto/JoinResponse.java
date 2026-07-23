package za.co.jse.simplechatplatform.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record JoinResponse(
        String username,
        String roomId,
        Instant joinedAt) {
}
