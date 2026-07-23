package za.co.jse.simplechatplatform.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
public class ChatRoomSession {
    private final String roomId;
    private final String roomName;
    private final Set<String> users = ConcurrentHashMap.newKeySet();

    public ChatRoomSession(String roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public void addUser(String username) {
        users.add(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }
}
