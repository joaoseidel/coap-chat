package fun.seidel;

import static fun.seidel.Client.BASE_URL;
import static fun.seidel.Client.JSON_FORMAT;
import static fun.seidel.commands.Groups.getClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fun.seidel.model.Message;

public class UpdateTaskMessages extends TimerTask {
    private static final String GROUPS_MESSAGE_RESOURCE_URL = BASE_URL + "/groups-message";

    private static HashMap<UUID, Message> groupMessages = new HashMap<>();
    private UUID uuid;

    public UpdateTaskMessages(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    @Override
    public void run() {
        try {
            CoapClient client = getClient().setURI(GROUPS_MESSAGE_RESOURCE_URL + "?" + uuid);
            CoapResponse get = client.get(JSON_FORMAT);

            if (get.isSuccess()) {
                List<Message> messages = new Gson()
                        .fromJson(get.getResponseText(), new TypeToken<ArrayList<Message>>() {
                        }.getType());

                if (groupMessages.size() != messages.size()) {
                    messages.forEach(message -> {
                        if (!groupMessages.containsKey(message.getUuid())) {
                            System.out.println(message.getMessage());
                            groupMessages.put(message.getUuid(), message);
                        }
                    });
                }
            }
        } catch (ConnectorException | IOException connectorException) {
            connectorException.printStackTrace();
        }
    }
}