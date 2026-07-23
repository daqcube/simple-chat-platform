package za.co.jse.simplechatplatform.messaging;

import org.junit.jupiter.api.Test;
import za.co.jse.simplechatplatform.domain.ChatMessage;
import za.co.jse.simplechatplatform.exception.MessageQueueFullException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ChatMessageProducerTest {
    private final ChatMessageProducer producer = new ChatMessageProducer();

    @Test
    void shouldPublishAndConsumeMessage() throws InterruptedException {
        ChatMessage message = ChatMessage.builder()
                .username("john")
                .content("hello")
                .roomId("developers")
                .build();

        producer.publish(message);

        ChatMessage consumed = producer.consume();

        assertNotNull(consumed);
        assertEquals("john", consumed.getUsername());
        assertEquals("hello", consumed.getContent());
        assertEquals("developers", consumed.getRoomId());
    }


    @Test
    void shouldConsumeMessagesInOrder() throws InterruptedException {
        //Given
        ChatMessage first = ChatMessage.builder()
                .content("first")
                .build();

        ChatMessage second = ChatMessage.builder()
                .content("second")
                .build();

        //SUT
        producer.publish(first);
        producer.publish(second);

        //Assertions
        assertEquals("first", producer.consume().getContent());
        assertEquals("second", producer.consume().getContent());
    }

    @Test
    void shouldThrowExceptionWhenQueueIsFull() throws InterruptedException {
        //Given
        BlockingQueue<ChatMessage> queue = mock(BlockingQueue.class);
        ChatMessageProducer messageProducer = new ChatMessageProducer(queue);

        ChatMessage message = ChatMessage.builder()
                .content("hello")
                .build();
        //When
        when(queue.offer(any(ChatMessage.class), anyLong(), any(TimeUnit.class)))
                .thenReturn(false);

        //SUT
        MessageQueueFullException exception = assertThrows(MessageQueueFullException.class, () -> messageProducer.publish(message));

        //Assertions
        assertEquals("Message queue is full, please try again shortly", exception.getMessage());
        verify(queue).offer(message, ChatMessageProducer.OFFER_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Test
    void shouldRestoreInterruptFlagAndThrowRuntimeExceptionWhenPublishIsInterrupted() throws InterruptedException {
        //Given
        BlockingQueue<ChatMessage> queue = mock(BlockingQueue.class);
        ChatMessageProducer messageProducer = new ChatMessageProducer(queue);

        ChatMessage message = ChatMessage.builder()
                .content("hello")
                .build();

        //When
        when(queue.offer(any(ChatMessage.class), anyLong(), any(TimeUnit.class)))
                .thenThrow(new InterruptedException());


        RuntimeException exception = assertThrows(RuntimeException.class, () -> messageProducer.publish(message));

        assertInstanceOf(InterruptedException.class, exception.getCause());

        assertTrue(Thread.currentThread().isInterrupted());

        // Clear interrupt status so other tests don't fail
        Thread.interrupted();
    }

    @Test
    void shouldWaitUntilMessageIsAvailable() throws InterruptedException {
        Thread producerThread = new Thread(() -> {
            try {
                Thread.sleep(100);
                producer.publish(
                        ChatMessage.builder()
                                .content("delayed message")
                                .build()
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producerThread.start();

        ChatMessage message = producer.consume();

        assertEquals("delayed message", message.getContent());
        producerThread.join();
    }


    @Test
    void shouldHandleMultipleMessages() throws InterruptedException {
        int messages = 10;

        for (int i = 0; i < messages; i++) {
            producer.publish(
                    ChatMessage.builder()
                            .content("message-" + i)
                            .build());
        }

        for (int i = 0; i < messages; i++) {
            assertEquals("message-" + i, producer.consume().getContent());
        }
    }
}