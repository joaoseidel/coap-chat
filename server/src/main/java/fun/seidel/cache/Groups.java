package fun.seidel.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fun.seidel.model.Group;
import fun.seidel.model.Message;

public class Groups {
    private static final List<Group> groups = new ArrayList<>();

    public static boolean add(Group group) {
        return groups.add(group);
    }

    public static void remove(UUID uuid) {
        find(uuid).ifPresent(groups::remove);
    }

    public static Optional<Group> find(UUID uuid) {
        return groups.stream()
                .filter(group -> group.getUuid().equals(uuid))
                .findFirst();
    }

    public static List<Group> findAll() {
        return groups;
    }

    public static void addMessage(UUID uuid, Message message) {
        find(uuid).ifPresent(group -> {
            List<Message> messages = group.getMessages();
            messages.add(message);
            group.setMessages(messages);
        });
    }
}
