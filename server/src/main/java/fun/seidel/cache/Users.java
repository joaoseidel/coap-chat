package fun.seidel.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fun.seidel.model.User;

public class Users {
    private static final List<User> users = new ArrayList<>();

    public static boolean add(User group) {
        return users.add(group);
    }

    public static void remove(String username) {
        find(username).ifPresent(users::remove);
    }

    public static Optional<User> find(String username) {
        return users.stream()
                .filter(group -> group.getUsername().equals(username))
                .findFirst();
    }

    public static List<User> findAll() {
        return users;
    }
}
