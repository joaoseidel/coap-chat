package fun.seidel.commands;

import static fun.seidel.Client.BASE_URL;
import static fun.seidel.Client.JSON_FORMAT;
import static fun.seidel.commands.Groups.Connect;
import static fun.seidel.commands.Groups.Create;
import static fun.seidel.commands.Groups.Delete;
import static fun.seidel.commands.Groups.List;
import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.Timer;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fun.seidel.Client;
import fun.seidel.UpdateTaskGroupMessages;
import fun.seidel.model.Group;
import fun.seidel.model.Message;

@Command(
        name = "groups",
        aliases = "g",
        subcommands = {Connect.class, Delete.class, Create.class, List.class}
)
public class Groups implements Runnable {
    private static final String GROUPS_RESOURCE_URL = BASE_URL + "/groups";
    private static final String GROUPS_MESSAGE_RESOURCE_URL = BASE_URL + "/groups-message";

    private static CoapClient client = null;
    private static String loggedUsername;

    public Groups(CoapClient client) {
        Groups.client = client;
        Groups.loggedUsername = Client.currentUser.getUsername();
    }

    public static CoapClient getClient() {
        return client;
    }

    @Override
    public void run() {
        System.out.println("Groups commands:");
        System.out.println("\t(g)roups (c)onnect {uuid}");
        System.out.println("\t(g)roups delete(rm) {uuid}");
        System.out.println("\t(g)roups create(add) {name}");
        System.out.println("\t(g)roups list(ls)");
    }

    @Command(name = "connect", aliases = "c")
    static class Connect implements Runnable {

        @Option(names = {"-u", "--uuid"})
        String uuid;

        @Override
        public void run() {
            try {
                if (Objects.isNull(uuid) || uuid.isEmpty()) {
                    System.out.println("UUID cannot be empty!");
                    return;
                }
                System.out.println("Connecting...");

                CoapClient client = getClient().setURI(GROUPS_RESOURCE_URL + "?" + uuid);
                CoapResponse get = client.get(JSON_FORMAT);

                if (get.isSuccess()) {
                    Group group = new Gson().fromJson(get.getResponseText(), Group.class);
                    System.out.println("Connected to " + group.getName() + ".");

                    Timer timer = new Timer();
                    timer.schedule(new UpdateTaskGroupMessages(uuid, loggedUsername), 0, 1000);

                    while (true) {
                        Scanner scan = new Scanner(System.in);

                        if (scan.hasNext()) {
                            Message message = new Message()
                                    .setMessage(scan.nextLine())
                                    .setSender(loggedUsername);

                            CoapClient sender = getClient().setURI(GROUPS_MESSAGE_RESOURCE_URL + "?" + uuid);
                            sender.post(new Gson().toJson(message), JSON_FORMAT);
                        }
                    }
                }
            } catch (ConnectorException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Command(name = "delete", aliases = "rm")
    static class Delete implements Runnable {

        @Option(names = {"-u", "--uuid"})
        String uuid;

        @Override
        public void run() {
            try {
                if (Objects.isNull(uuid) || uuid.isEmpty()) {
                    System.out.println("UUID cannot be empty!");
                    return;
                }

                CoapClient client = getClient().setURI(GROUPS_RESOURCE_URL + "?" + uuid);
                CoapResponse delete = client.delete();

                if (delete.isSuccess()) {
                    System.out.println("Group " + uuid + " removed!");
                }
            } catch (ConnectorException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Command(name = "create", aliases = "add")
    static class Create implements Runnable {

        @Option(names = {"-n", "--name"})
        String name;

        @Override
        public void run() {
            try {
                if (Objects.isNull(name) || name.isEmpty()) {
                    System.out.println("Name cannot be empty!");
                    return;
                }

                Group group = new Group()
                        .setName(name);

                CoapClient client = getClient().setURI(GROUPS_RESOURCE_URL);
                CoapResponse post = client.post(new Gson().toJson(group), JSON_FORMAT);

                Group createdGroup = new Gson().fromJson(post.getResponseText(), Group.class);

                if (post.isSuccess()) {
                    System.out.println("Group " + name + " (" + createdGroup.getUuid() + ") created!");
                }
            } catch (ConnectorException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Command(name = "list", aliases = "ls")
    static class List implements Runnable {

        @Override
        public void run() {
            try {
                CoapClient client = getClient().setURI(GROUPS_RESOURCE_URL);
                CoapResponse get = client.get(JSON_FORMAT);

                if (get.isSuccess()) {
                    System.out.println("Available groups:");
                    ArrayList<Group> groups = new Gson()
                            .fromJson(get.getResponseText(), new TypeToken<ArrayList<Group>>() {
                            }.getType());
                    groups.forEach(group -> System.out.println("\t" + group.getName() + "\t" + group.getUuid()));
                }
            } catch (ConnectorException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}

