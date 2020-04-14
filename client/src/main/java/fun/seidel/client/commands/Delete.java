package fun.seidel.client.commands;

import static fun.seidel.client.components.COAPClientComponent.GROUPS_RESOURCE_URL;

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

import fun.seidel.client.components.COAPClientComponent;
import fun.seidel.client.helper.PromptColor;
import fun.seidel.client.helper.ShellHelper;

@ShellComponent
public class Delete {

    @Autowired
    private COAPClientComponent coapClientComponent;

    @Autowired
    private ShellHelper shellHelper;

    @ShellMethod(key = {"delete group", "rm group"}, value = "Delete a group.", group = "Groups")
    @ShellMethodAvailability("authenticateAvailability")
    public void deleteGroup(
            @ShellOption(arity = 1, help = "Group UUID") String uuid
    ) throws ConnectorException, IOException {
        if (Objects.isNull(uuid) || uuid.isEmpty()) {
            shellHelper.printError("UUID cannot be null!");
            return;
        }

        CoapClient client = coapClientComponent.get().setURI(GROUPS_RESOURCE_URL + "?" + uuid);
        CoapResponse delete = client.delete();

        if (delete.isSuccess()) {
            StringBuilder response = new StringBuilder()
                    .append("Group ")
                    .append(shellHelper.getColored(uuid, PromptColor.CYAN))
                    .append(" was removed!");
            shellHelper.print(response.toString());
            return;
        }

        shellHelper.printError("Could not remove this group right now.");
    }

    private Availability authenticateAvailability() {
        return coapClientComponent.isConnected() ?
                Availability.available() : Availability.unavailable("you are not connected.");
    }
}
