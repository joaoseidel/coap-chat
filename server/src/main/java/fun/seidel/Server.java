package fun.seidel;

import java.util.UUID;

import org.eclipse.californium.core.CoapServer;

import fun.seidel.cache.Groups;
import fun.seidel.model.Group;
import fun.seidel.resources.GroupMessageResource;

public class Server extends CoapServer {

    public Server() {
        add(new GroupMessageResource("groups"));

        Group globalChat = new Group()
                .setUuid(UUID.randomUUID())
                .setName("Global Chat");
        Groups.add(globalChat);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
