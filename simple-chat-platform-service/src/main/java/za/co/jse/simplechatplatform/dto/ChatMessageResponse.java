package za.co.jse.simplechatplatform.dto;

import za.co.jse.simplechatplatform.enums.MessageType;

import java.time.Instant;

public record ChatMessageResponse(String username,
                                  String content,
                                  Instant timestamp,
                                  MessageType type) {
}
