package fun.seidel;

import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.californium.core.CoapServer;

import fun.seidel.cache.Groups;
import fun.seidel.model.Group;
import fun.seidel.resources.GroupMessageResource;
import fun.seidel.resources.GroupResource;

public class Server extends CoapServer {

    public Server() {
        add(
                new GroupResource("groups"),
                new GroupMessageResource("groups-message")
        );

        Group globalChat = new Group()
                .setUuid(UUID.randomUUID())
                .setName("Global Chat")
                .setDefaultGroup(true);
        Groups.add(globalChat);
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();

        Server server = new Server();
        server.start();
    }
}
