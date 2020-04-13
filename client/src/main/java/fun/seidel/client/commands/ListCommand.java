package fun.seidel.client.commands;

import static fun.seidel.client.components.COAPClientComponent.GROUPS_RESOURCE_URL;
import static fun.seidel.client.components.COAPClientComponent.JSON_FORMAT_CODE;

import java.io.IOException;
import java.util.List;

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
            StringBuilder result = new StringBuilder();
            result.append("Available groups\n");

            List<Group> groups = new Gson().fromJson(get.getResponseText(), new TypeToken<List<Group>>() {
            }.getType());

            groups.forEach(group -> result
                    .append(group.getName()).append("\t")
                    .append(shellHelper.getColored(group.getUuid().toString(), PromptColor.CYAN))
                    .append("\n"));
            shellHelper.print(result.toString());
            return;
        }

        shellHelper.printError("No groups found.");
    }

    @ShellMethod(key = {"list users", "ls users"}, value = "List available groups", group = "Users")
    @ShellMethodAvailability("authenticateAvailability")
    public String listUsers() {
        return "users";
    }

    private Availability authenticateAvailability() {
        return coapClientComponent.isConnected() ?
                Availability.available() : Availability.unavailable("you are not connected.");
    }
}
