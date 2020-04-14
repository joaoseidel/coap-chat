package fun.seidel;

import java.util.Timer;
import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.californium.core.CoapServer;

import fun.seidel.cache.Groups;
import fun.seidel.model.Group;
import fun.seidel.resources.GroupMessageResource;
import fun.seidel.resources.GroupResource;
import fun.seidel.resources.PrivateMessageResource;
import fun.seidel.resources.UsersResource;

public class Server extends CoapServer {

    public Server() {
        add(
                new GroupResource("groups"),
                new GroupMessageResource("groups-message"),
                new UsersResource("users"),
                new PrivateMessageResource("private-messages")
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
