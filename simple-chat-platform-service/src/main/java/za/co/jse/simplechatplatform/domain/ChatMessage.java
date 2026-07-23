package za.co.jse.simplechatplatform.domain;

import lombok.*;
import za.co.jse.simplechatplatform.enums.MessageType;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private MessageType type;
    private String username;
    private String content;
    private Instant timestamp;
    private String roomId;
}
