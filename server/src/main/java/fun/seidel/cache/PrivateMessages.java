package fun.seidel.cache;

import java.util.ArrayList;
import java.util.List;

import fun.seidel.model.Message;

public class PrivateMessages {
    private static final List<Message> messages = new ArrayList<>();

    public static boolean add(Message message) {
        return messages.add(message);
    }

    public static List<Message> findAll() {
        return messages;
    }
}
