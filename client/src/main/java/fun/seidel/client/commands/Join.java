package fun.seidel.client.commands;

import static fun.seidel.client.components.COAPClientComponent.GROUPS_RESOURCE_URL;
import static fun.seidel.client.components.COAPClientComponent.JSON_FORMAT_CODE;
import static fun.seidel.client.components.COAPClientComponent.USERS_RESOURCE_URL;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fun.seidel.client.components.COAPClientComponent;
import fun.seidel.client.components.GroupMessageComponent;
import fun.seidel.client.components.PrivateMessageComponents;
import fun.seidel.client.helper.PromptColor;
import fun.seidel.client.helper.ShellHelper;
import fun.seidel.model.Group;

@ShellComponent
public class Join {

    @Autowired
    private GroupMessageComponent groupMessageComponent;

    @Autowired
    private PrivateMessageComponents privateMessageComponents;

    @Autowired
    private COAPClientComponent coapClientComponent;

    @Autowired
    @Lazy
    private LineReader lineReader;

    @Autowired
    private ShellHelper shellHelper;

    @ShellMethod(key = "join group", value = "Join into a conversation group.", group = "Groups")
    @ShellMethodAvailability("authenticateAvailability")
    public void joinGroup(
            @ShellOption(arity = 1, help = "Group uuid") String uuid
    ) throws ConnectorException, IOException {
        if (Objects.isNull(uuid) || uuid.isEmpty()) {
            shellHelper.printError("UUID cannot be null!");
            return;
        }
        shellHelper.print("Connecting...");

        CoapClient client = coapClientComponent.get().setURI(GROUPS_RESOURCE_URL + "?" + uuid);
        CoapResponse get = client.get(JSON_FORMAT_CODE);

        if (get.isSuccess()) {
            Group group = new Gson().fromJson(get.getResponseText(), Group.class);

            shellHelper.print("Connected to " + shellHelper.getColored(group.getName(), PromptColor.CYAN) + ".");

            groupMessageComponent.clearCachedMessages(uuid);
            groupMessageComponent.getGroupMessages();
            while (true) {
                String message = lineReader.readLine();
                if (Objects.nonNull(message) && !message.isEmpty()) {
                    if (message.equalsIgnoreCase("/quit")) {
                        groupMessageComponent.exit();
                        break;
                    }

                    groupMessageComponent.sendMessage(uuid, message);
                }
            }

            shellHelper.printWarning("Disconnected.");
            return;
        }

        shellHelper.printError("Could not connect to the group!");
    }

    @ShellMethod(key = "join private", value = "Join a private conversation with an user.", group = "Users")
    @ShellMethodAvailability("authenticateAvailability")
    public void joinPrivate(
            @ShellOption(arity = 1, help = "Target username") String username
    ) throws ConnectorException, IOException {
        if (Objects.isNull(username) || username.isEmpty()) {
            shellHelper.printError("Username cannot be null!");
            return;
        }
        shellHelper.print("Trying to chat with " + username + "...");

        CoapClient client = coapClientComponent.get().setURI(USERS_RESOURCE_URL);
        CoapResponse get = client.get(JSON_FORMAT_CODE);

        if (get.isSuccess()) {
            Set<String> users = new Gson().fromJson(get.getResponseText(), new TypeToken<Set<String>>() {
            }.getType());

            if (users.stream().noneMatch(username::equalsIgnoreCase)) {
                shellHelper.printError("No username like " + username + " has ever joined the server.");
                return;
            }

            shellHelper.print("Now you are talking with " + shellHelper.getColored(username, PromptColor.CYAN) + ".");

            privateMessageComponents.clearCachedMessages(username);
            privateMessageComponents.handleMessages();
            while (true) {
                String message = lineReader.readLine();
                if (Objects.nonNull(message) && !message.isEmpty()) {
                    if (message.equalsIgnoreCase("/quit")) {
                        privateMessageComponents.exit();
                        break;
                    }

                    privateMessageComponents.sendMessage(username, message);
                }
            }

            shellHelper.printWarning("Disconnected.");
            return;
        }

        shellHelper.printError("User not online!");
    }

    private Availability authenticateAvailability() {
        return coapClientComponent.isConnected() ?
                Availability.available() : Availability.unavailable("you are not connected.");
    }
}
