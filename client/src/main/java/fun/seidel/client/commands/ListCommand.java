package fun.seidel.client.commands;

import static fun.seidel.client.components.COAPClientComponent.GROUPS_RESOURCE_URL;
import static fun.seidel.client.components.COAPClientComponent.JSON_FORMAT_CODE;
import static fun.seidel.client.components.COAPClientComponent.USERS_RESOURCE_URL;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fun.seidel.client.components.COAPClientComponent;
import fun.seidel.client.helper.PromptColor;
import fun.seidel.client.helper.ShellHelper;
import fun.seidel.model.Group;

@ShellComponent
public class ListCommand {

    @Autowired
    private COAPClientComponent coapClientComponent;

    @Autowired
    private ShellHelper shellHelper;

    @ShellMethod(key = {"list groups", "ls groups"}, value = "List available groups", group = "Groups")
    @ShellMethodAvailability("authenticateAvailability")
    public void listGroups() throws ConnectorException, IOException {
        CoapClient client = coapClientComponent.get().setURI(GROUPS_RESOURCE_URL);
        CoapResponse get = client.get(JSON_FORMAT_CODE);

        if (get.isSuccess()) {
            List<Group> groups = new Gson().fromJson(get.getResponseText(), new TypeToken<List<Group>>() {
            }.getType());

            shellHelper.print("Available groups");
            String result = groups.stream()
                    .map(group -> new StringBuilder()
                            .append(group.getName()).append("\t")
                            .append(shellHelper.getColored(group.getUuid().toString(), PromptColor.CYAN)))
                    .collect(Collectors.joining("\n"));
            shellHelper.print(result);
            return;
        }

        shellHelper.printError("No groups found.");
    }

    @ShellMethod(key = {"list users", "ls users"}, value = "List available groups", group = "Users")
    @ShellMethodAvailability("authenticateAvailability")
    public void listUsers() throws ConnectorException, IOException {
        CoapClient client = coapClientComponent.get().setURI(USERS_RESOURCE_URL);
        CoapResponse get = client.get(JSON_FORMAT_CODE);

        if (get.isSuccess()) {
            Set<String> users = new Gson().fromJson(get.getResponseText(), new TypeToken<Set<String>>() {
            }.getType());

            List<String> availableUsers = users.stream()
                    .filter(Objects::nonNull)
                    .filter(user -> !user.equalsIgnoreCase(coapClientComponent.getUsername()))
                    .collect(Collectors.toList());

            if (availableUsers.isEmpty()) {
                shellHelper.printError("No available users found.");
                return;
            }

            shellHelper.print("Available users");

            String result = availableUsers.stream()
                    .map(user -> new StringBuilder()
                            .append(user).append("\t")
                            .append(shellHelper.getColored(user, PromptColor.CYAN)))
                    .collect(Collectors.joining("\n"));
            shellHelper.print(result);
            return;
        }

        shellHelper.printError("No available users found.");
    }

    private Availability authenticateAvailability() {
        return coapClientComponent.isConnected() ?
                Availability.available() : Availability.unavailable("you are not connected.");
    }
}
