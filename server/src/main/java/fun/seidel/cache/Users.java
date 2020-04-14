package fun.seidel.cache;

import java.util.HashSet;
import java.util.Set;

public class Users {
    private static final HashSet<String> users = new HashSet<>();

    public static boolean add(String username) {
        if (users.stream().noneMatch(username::equalsIgnoreCase))
            return users.add(username);
        return false;
    }

    public static void remove(String username) {
        users.stream()
                .filter(name -> name.equalsIgnoreCase(username))
                .findAny()
                .ifPresent(users::remove);
    }

    public static boolean exists(String username) {
        return users.stream().anyMatch(username::equalsIgnoreCase);
    }

    public static Set<String> findAll() {
        return users;
    }
}
