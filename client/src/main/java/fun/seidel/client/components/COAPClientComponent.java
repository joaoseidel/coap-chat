package fun.seidel.client.components;

import java.io.IOException;
import java.util.Objects;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import fun.seidel.client.helper.ShellHelper;

@Component
@EnableScheduling
public class COAPClientComponent {
    private static final String TIME_ZONE = "America/Sao_Paulo";

    public static final int JSON_FORMAT_CODE = 50;
    public static String GROUPS_RESOURCE_URL;
    public static String GROUPS_MESSAGE_RESOURCE_URL;
    public static String USERS_RESOURCE_URL;
    public static String PRIVATE_MESSAGES_RESOURCE_URL;

    private CoapClient client;
    private String username;

    @Autowired
    private ShellHelper shellHelper;

    public CoapClient get() {
        return client;
    }

    public String getUsername() {
        return username;
    }

    public void connect(String url, String username) throws ConnectorException, IOException {
        shellHelper.print("Connecting...");
        CoapClient client = new CoapClient("coap://" + url);
        if (client.ping()) {
            setup(username, client);

            CoapResponse coapResponse = client.setURI(USERS_RESOURCE_URL).post(new Gson().toJson(username), JSON_FORMAT_CODE);
            if (coapResponse.isSuccess()) {
                shellHelper.print("Connected to coap://" + url + " as " + username + ".");
                return;
            }
            shellHelper.printError("Failed to connect to coap://" + url + " as " + username + ".");
        }
    }

    private void setup(String username, CoapClient client) {
        this.client = client;
        this.username = username;
        GROUPS_RESOURCE_URL = client.getURI() + "/groups";
        GROUPS_MESSAGE_RESOURCE_URL = client.getURI() + "/groups-message";
        USERS_RESOURCE_URL = client.getURI() + "/users";
        PRIVATE_MESSAGES_RESOURCE_URL = client.getURI() + "/private-messages";
    }

    public boolean isConnected() {
        return Objects.nonNull(client) && Objects.nonNull(username) && !username.isEmpty();
    }
}
