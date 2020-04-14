package fun.seidel.client.components;

import static fun.seidel.client.components.COAPClientComponent.JSON_FORMAT_CODE;
import static fun.seidel.client.components.COAPClientComponent.PRIVATE_MESSAGES_RESOURCE_URL;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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

import fun.seidel.client.helper.PromptColor;
import fun.seidel.client.helper.ShellHelper;
import fun.seidel.model.Message;

@Component
@EnableScheduling
public class PrivateMessageComponents {

    private static final String TIME_ZONE = "America/Sao_Paulo";
    private static final HashMap<UUID, Message> cachedMessages = new HashMap<>();
    private boolean popup;
    private String chatingWith;

    @Autowired
    private COAPClientComponent coapClientComponent;

    @Autowired
    private ShellHelper shellHelper;

    @Scheduled(cron = "*/1 * * * * *", zone = TIME_ZONE)
    public void handleMessages() throws ConnectorException, IOException {
        if (Objects.isNull(chatingWith) || chatingWith.isEmpty()) {
            return;
        }

        String loggedUsername = coapClientComponent.getUsername();

        CoapClient client = coapClientComponent.get().setURI(PRIVATE_MESSAGES_RESOURCE_URL);
        CoapResponse get = client.get(JSON_FORMAT_CODE);

        if (get.isSuccess()) {
            List<Message> messages = new Gson().fromJson(get.getResponseText(), new TypeToken<List<Message>>() {
            }.getType());

            if (cachedMessages.isEmpty() && !popup) {
                messages.forEach(message -> {
                    if (!cachedMessages.containsKey(message.getUuid())) {
                        if (message.getSender().equalsIgnoreCase(chatingWith) && message.getDestination().equalsIgnoreCase(loggedUsername)) {
                            shellHelper.print(shellHelper.getColored(message.getSender(), PromptColor.CYAN) + ": " + message.getMessage());
                            System.out.println(message.getSender() + ": " + message.getMessage());
                            cachedMessages.put(message.getUuid(), message);
                        }

                        if (message.getSender().equalsIgnoreCase(loggedUsername) && message.getDestination().equalsIgnoreCase(chatingWith)) {
                            System.out.println(message.getMessage());
                            cachedMessages.put(message.getUuid(), message);
                        }
                    }
                });
                popup = true;
                return;
            }

            if (cachedMessages.size() != messages.size()) {
                messages.stream()
                        .filter(message -> !message.getSender().equalsIgnoreCase(loggedUsername))
                        .filter(message -> message.getSender().equalsIgnoreCase(chatingWith))
                        .filter(message -> message.getDestination().equalsIgnoreCase(loggedUsername))
                        .forEach(message -> {
                            if (!cachedMessages.containsKey(message.getUuid())) {
                                shellHelper.print(shellHelper.getColored(message.getSender(), PromptColor.CYAN) + ": " + message.getMessage());
                                cachedMessages.put(message.getUuid(), message);
                            }
                        });
            }
        }
    }

    public void clearCachedMessages(String username) {
        cachedMessages.clear();
        this.popup = false;
        this.chatingWith = username;
    }

    public void exit() {
        cachedMessages.clear();
        this.popup = false;
        this.chatingWith = null;
    }

    public void sendMessage(String username, String message) throws ConnectorException, IOException {
        Message sendMessage = new Message()
                .setMessage(message)
                .setDestination(username)
                .setSender(coapClientComponent.getUsername());

        CoapClient sender = coapClientComponent.get().setURI(PRIVATE_MESSAGES_RESOURCE_URL + "?" + username);
        sender.post(new Gson().toJson(sendMessage), JSON_FORMAT_CODE);
    }
}
