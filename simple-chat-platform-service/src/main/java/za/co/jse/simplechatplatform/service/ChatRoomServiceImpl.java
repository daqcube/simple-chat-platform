package za.co.jse.simplechatplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import za.co.jse.simplechatplatform.domain.ChatMessage;
import za.co.jse.simplechatplatform.domain.ChatRoomSession;
import za.co.jse.simplechatplatform.dto.ChatMessageRequest;
import za.co.jse.simplechatplatform.dto.ChatRoomRequest;
import za.co.jse.simplechatplatform.dto.ChatRoomResponse;
import za.co.jse.simplechatplatform.dto.JoinResponse;
import za.co.jse.simplechatplatform.mapper.ChatMessageMapper;
import za.co.jse.simplechatplatform.messaging.ChatMessageProducer;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ConcurrentHashMap<String, ChatRoomSession> rooms = new ConcurrentHashMap<>();
    private final ChatMessageProducer producer;
    private final ChatMessageMapper mapper;

    @Value("${jse.chat.default-room}")
    private String defaultRoom;

    public ChatRoomServiceImpl(ChatMessageProducer producer,
                               ChatMessageMapper mapper) {
        this.producer = producer;
        this.mapper = mapper;
    }

    @Override
    public JoinResponse join(ChatRoomRequest request) {
        String roomId = Objects.isNull(request.roomId()) || request.roomId().isEmpty() ? defaultRoom : request.roomId();

        ChatRoomSession room = rooms.computeIfAbsent(roomId, ChatRoomSession::new);
        room.addUser(request.username());

        ChatMessage message = mapper.toJoinMessage(request.username(), room.getRoomId());
        producer.publish(message);
        log.info("User with username {} JOINED room {}", request.username(), room.getRoomId());
        return new JoinResponse(request.username(), room.getRoomId(), Instant.now());
    }

    @Override
    public void sendMessage(ChatMessageRequest request) {
        ChatMessage message = mapper.toChatMessage(request);
        producer.publish(message);
        log.info("Message queued for username {} for room {}", request.username(), request.roomId());
    }

    @Override
    public void leave(ChatRoomRequest request) {
        ChatRoomSession room = rooms.get(request.roomId());
        if (Objects.isNull(room)) return;

        room.removeUser(request.username());
        ChatMessage message = mapper.toLeftMessage(request.username(), room.getRoomId());
        producer.publish(message);
        log.info("User with username {} LEFT room {}", request.username(), room.getRoomId());
    }

    @Override
    public Set<ChatRoomResponse> getRooms() {
        return rooms.values()
                .stream()
                .map(room -> new ChatRoomResponse(
                        room.getRoomId(),
                        room.getUsers()
                ))

                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getRoomUsers(String roomId) {
        ChatRoomSession room = rooms.get(roomId);
        if (Objects.isNull(room)) {
            return Set.of();
        }
        return room.getUsers();
    }
}
