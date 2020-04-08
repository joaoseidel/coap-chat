package fun.seidel.model;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {

    private UUID uuid;
    private String name;
    private UUID messagingWith;

    public UUID getUuid() {
        return uuid;
    }

    public User setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
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
