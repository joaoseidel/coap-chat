package fun.seidel.client.commands;

import static fun.seidel.client.components.COAPClientComponent.GROUPS_RESOURCE_URL;
import static fun.seidel.client.components.COAPClientComponent.JSON_FORMAT_CODE;

import java.io.IOException;
import java.util.Objects;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import com.google.gson.Gson;

import fun.seidel.client.components.COAPClientComponent;
import fun.seidel.client.helper.PromptColor;
import fun.seidel.client.helper.ShellHelper;
import fun.seidel.model.Group;

@ShellComponent
public class Create {

    @Autowired
    private COAPClientComponent coapClientComponent;

    @Autowired
    private ShellHelper shellHelper;

    @ShellMethod(key = {"create group", "add group"}, value = "Create a conversation group.", group = "Groups")
    @ShellMethodAvailability("authenticateAvailability")
    public void createGroup(
            @ShellOption(arity = 1, help = "Group name") String name
    ) throws ConnectorException, IOException {
        if (Objects.isNull(name) || name.isEmpty()) {
            shellHelper.printError("Name cannot be null!");
            return;
        }

        Group group = new Group()
                .setName(name);

        CoapClient client = coapClientComponent.get().setURI(GROUPS_RESOURCE_URL);
        CoapResponse post = client.post(new Gson().toJson(group), JSON_FORMAT_CODE);

        Group createdGroup = new Gson().fromJson(post.getResponseText(), Group.class);

        if (post.isSuccess()) {
            StringBuilder response = new StringBuilder()
                    .append("Group ").append(name)
                    .append(" (")
                    .append(shellHelper.getColored(createdGroup.getUuid().toString(), PromptColor.CYAN))
                    .append(")").append(" was created!");
            shellHelper.printError(response.toString());
            return;
        }

        shellHelper.printError("Could not create a group right now");
    }

    private Availability authenticateAvailability() {
        return coapClientComponent.isConnected() ?
                Availability.available() : Availability.unavailable("you are not connected.");
    }
}
