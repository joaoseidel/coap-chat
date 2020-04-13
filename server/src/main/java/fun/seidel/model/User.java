package fun.seidel.model;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {

    private UUID uuid;
    private String username;
    private String privateMessaging;

    public UUID getUuid() {
        return uuid;
    }

    public User setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPrivateMessaging() {
        return privateMessaging;
    }

    public User setPrivateMessaging(String privateMessaging) {
        this.privateMessaging = privateMessaging;
        return this;
    }
}
