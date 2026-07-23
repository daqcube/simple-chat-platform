package za.co.jse.simplechatplatform.exception;

public class MessageQueueFullException extends RuntimeException {

    public MessageQueueFullException(String message) {
        super(message);
    }
}
