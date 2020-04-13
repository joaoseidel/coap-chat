package fun.seidel.client.commands;

import static fun.seidel.client.components.COAPClientComponent.GROUPS_RESOURCE_URL;
import static fun.seidel.client.components.COAPClientComponent.JSON_FORMAT_CODE;

import java.io.IOException;
import java.util.Objects;

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

import fun.seidel.client.components.COAPClientComponent;
import fun.seidel.client.components.GroupMessageComponent;
import fun.seidel.client.helper.PromptColor;
import fun.seidel.client.helper.ShellHelper;
import fun.seidel.model.Group;

@ShellComponent
public class Join {

    @Autowired
    private GroupMessageComponent groupMessageComponent;

    @Autowired
    private COAPClientComponent coapClientComponent;

    @Autowired
    @Lazy
    private LineReader lineReader;

    @Autowired
    private ShellHelper shellHelper;

    @ShellMethod(key = "join group", value = "Join into a conversation group.", group = "Groups")
    @ShellMethodAvailability
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

    private Availability authenticateAvailability() {
        return coapClientComponent.isConnected() ?
                Availability.available() : Availability.unavailable("you are not connected.");
    }
}
