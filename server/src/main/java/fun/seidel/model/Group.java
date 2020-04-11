package fun.seidel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group implements Serializable {

    private UUID uuid;
    private String name;
    private List<User> connectedUsers = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private transient boolean defaultGroup;

    public UUID getUuid() {
        return uuid;
    }

    public Group setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getName() {
        return name;
    }

    public Group setName(String name) {
        this.name = name;
        return this;
    }

    public List<User> getConnectedUsers() {
        return connectedUsers;
    }

    public Group setConnectedUsers(List<User> connectedUsers) {
        this.connectedUsers = connectedUsers;
        return this;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Group setMessages(List<Message> messages) {
        this.messages = messages;
        return this;
    }

    public boolean isDefaultGroup() {
        return defaultGroup;
    }

    public Group setDefaultGroup(boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
        return this;
    }
}
