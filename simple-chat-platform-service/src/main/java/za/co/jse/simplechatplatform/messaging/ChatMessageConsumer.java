package za.co.jse.simplechatplatform.messaging;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import za.co.jse.simplechatplatform.domain.ChatMessage;
import za.co.jse.simplechatplatform.service.ChatRoomService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageConsumer {
    private final ChatMessageQueue chatMessageQueue;
    private final ChatRoomBroadcaster broadcaster;
    private final ChatRoomService chatRoomService;
    private final AtomicBoolean running = new AtomicBoolean(true);

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "chat-message-consumer");
        thread.setDaemon(true);
        return thread;
    });

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        executor.submit(() -> {
            while (running.get()) {
                try {
                    ChatMessage message = chatMessageQueue.consume();
                    switch (message.getType()) {
                        case CHAT -> broadcaster.broadcastMessage(message.getRoomId(), message);
                        case JOINED, LEFT -> {
                            broadcaster.broadcastMessage(message.getRoomId(), message);
                            broadcaster.broadcastUsers(message.getRoomId(), chatRoomService.getRoomUsers(message.getRoomId()));
                        }
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.debug("Error consuming chat messages", ex);
                    break;
                }
            }
        });

    }

    @PreDestroy
    public void stop() {
        running.set(false);
        executor.shutdownNow();
    }

}