package za.co.jse.simplechatplatform.service;

import jakarta.annotation.PostConstruct;
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
import java.util.Comparator;
import java.util.LinkedHashSet;
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

    @PostConstruct
    void initRooms() {
        rooms.put("developers", new ChatRoomSession("developers", "Java Developers"));
        rooms.put("spring", new ChatRoomSession("spring", "Spring Boot"));
        rooms.put("general", new ChatRoomSession("general", "General"));
    }

    public ChatRoomServiceImpl(ChatMessageProducer producer,
                               ChatMessageMapper mapper) {
        this.producer = producer;
        this.mapper = mapper;
    }

    @Override
    public JoinResponse join(ChatRoomRequest request) {
        String roomId = Objects.isNull(request.roomId()) || request.roomId().isEmpty() ? defaultRoom : request.roomId();

        ChatRoomSession room = getRoomById(roomId);
        if (Objects.isNull(room)) {
            throw new IllegalArgumentException(String.format("Room %s does not exist ", request.roomId()));
        }
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
        ChatRoomSession room = getRoomById(request.roomId());
        if (Objects.isNull(room)) return;

        room.removeUser(request.username());
        ChatMessage message = mapper.toLeftMessage(request.username(), room.getRoomId());
        producer.publish(message);
        log.info("User with username {} LEFT room {}", request.username(), room.getRoomId());
    }

    private ChatRoomSession getRoomById(String roomId) {
        return rooms.get(roomId);
    }

    @Override
    public Set<ChatRoomResponse> getRooms() {
        return rooms.values()
                .stream()
                .map(room -> new ChatRoomResponse(
                        room.getRoomId(),
                        room.getRoomName(),
                        room.getUsers().size()
                ))
                .sorted(Comparator.comparing(ChatRoomResponse::name))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<String> getRoomUsers(String roomId) {
        ChatRoomSession room = getRoomById(roomId);
        if (Objects.isNull(room)) {
            return Set.of();
        }
        return room.getUsers();
    }
}
