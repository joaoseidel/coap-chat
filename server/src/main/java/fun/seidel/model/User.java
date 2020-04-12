package fun.seidel.model;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {

    private String username;
    private UUID messagingWith;

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public UUID getMessagingWith() {
        return messagingWith;
    }

    public User setMessagingWith(UUID messagingWith) {
        this.messagingWith = messagingWith;
        return this;
    }
}
