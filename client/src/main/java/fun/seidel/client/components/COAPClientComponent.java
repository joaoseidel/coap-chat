package fun.seidel.client.components;

import java.util.Objects;

import org.eclipse.californium.core.CoapClient;
import org.springframework.stereotype.Component;

@Component
public class COAPClientComponent {
    public static final int JSON_FORMAT_CODE = 50;
    public static String GROUPS_RESOURCE_URL;
    public static String GROUPS_MESSAGE_RESOURCE_URL;

    private CoapClient client;
    private String username;

    public CoapClient get() {
        return client;
    }

    public String getUsername() {
        return username;
    }

    public void connect(String url, String username) {
        System.out.println("Connecting...");
        CoapClient client = new CoapClient("coap://" + url);
        if (client.ping()) {
            setup(username, client);
            System.out.println("Connected to coap://" + url + " as " + username + ".");
        }
    }

    private void setup(String username, CoapClient client) {
        this.client = client;
        this.username = username;
        GROUPS_RESOURCE_URL = client.getURI() + "/groups";
        GROUPS_MESSAGE_RESOURCE_URL = client.getURI() + "/groups-message";
    }

    public boolean isConnected() {
        return Objects.nonNull(client) && Objects.nonNull(username) && !username.isEmpty();
    }
}
