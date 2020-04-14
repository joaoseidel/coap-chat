package fun.seidel.model;

import java.util.Date;
import java.util.UUID;

public class Message {

    private UUID uuid;
    private String sender;
    private String destination;
    private String message;
    private Date creationDate;

    public UUID getUuid() {
        return uuid;
    }

    public Message setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getSender() {
        return sender;
    }

    public Message setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public String getDestination() {
        return destination;
    }

    public Message setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Message setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }
}
