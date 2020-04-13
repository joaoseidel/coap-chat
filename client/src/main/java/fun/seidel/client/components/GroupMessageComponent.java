package fun.seidel.client.components;

import static fun.seidel.client.components.COAPClientComponent.GROUPS_MESSAGE_RESOURCE_URL;
import static fun.seidel.client.components.COAPClientComponent.JSON_FORMAT_CODE;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fun.seidel.model.Message;

@Component
@EnableScheduling
public class GroupMessageComponent {

    private static final String TIME_ZONE = "America/Sao_Paulo";
    private static final HashMap<UUID, Message> groupMessages = new HashMap<>();
    private boolean popup;
    private String uuid;

    @Autowired
    private COAPClientComponent coapClientComponent;

    @Scheduled(cron = "*/1 * * * * *", zone = TIME_ZONE)
    public void getGroupMessages() throws ConnectorException, IOException {
        CoapClient client = coapClientComponent.get().setURI(GROUPS_MESSAGE_RESOURCE_URL + "?" + uuid);
        CoapResponse get = client.get(JSON_FORMAT_CODE);

        if (get.isSuccess()) {
            List<Message> messages = new Gson().fromJson(get.getResponseText(), new TypeToken<List<Message>>() {
            }.getType());

            if (groupMessages.isEmpty() && !popup) {
                messages.forEach(message -> {
                    if (!groupMessages.containsKey(message.getUuid())) {
                        if (!message.getSender().equals(coapClientComponent.getUsername())) {
                            System.out.println(message.getSender() + ": " + message.getMessage());
                        } else {
                            System.out.println(message.getMessage());
                        }
                        groupMessages.put(message.getUuid(), message);
                    }
                });
                popup = true;
                return;
            }

            if (groupMessages.size() != messages.size()) {
                messages.stream()
                        .filter(message -> !message.getSender().equals(coapClientComponent.getUsername()))
                        .forEach(message -> {
                            if (!groupMessages.containsKey(message.getUuid())) {
                                System.out.println(message.getSender() + "\033[0m: " + message.getMessage());
                                groupMessages.put(message.getUuid(), message);
                            }
                        });
            }
        }
    }

    public void clearCachedMessages(String uuid) {
        groupMessages.clear();
        this.popup = false;
        this.uuid = uuid;
    }

    public void sendMessage(String uuid, String message) throws ConnectorException, IOException {
        Message sendMessage = new Message()
                .setMessage(message)
                .setSender(coapClientComponent.getUsername());

        CoapClient sender = coapClientComponent.get().setURI(GROUPS_MESSAGE_RESOURCE_URL + "?" + uuid);
        sender.post(new Gson().toJson(sendMessage), JSON_FORMAT_CODE);
    }
}
