package za.co.jse.simplechatplatform.messaging;

import org.springframework.stereotype.Component;
import za.co.jse.simplechatplatform.domain.ChatMessage;
import za.co.jse.simplechatplatform.exception.MessageQueueFullException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class ChatMessageProducer implements ChatMessageQueue {
    public static final int OFFER_TIMEOUT = 100;

    private final BlockingQueue<ChatMessage> queue;

    public ChatMessageProducer() {
        this.queue = new LinkedBlockingQueue<>();
    }

    ChatMessageProducer(BlockingQueue<ChatMessage> queue) {
        this.queue = queue;
    }

    @Override
    public void publish(ChatMessage message) {
        try {
            boolean accepted = queue.offer(message, OFFER_TIMEOUT, TimeUnit.MILLISECONDS);
            if (!accepted) throw new MessageQueueFullException("Message queue is full, please try again shortly");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChatMessage consume() throws InterruptedException {
        return queue.take();
    }
}
