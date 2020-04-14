package fun.seidel.client.commands;

import java.io.IOException;

import org.eclipse.californium.elements.exception.ConnectorException;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import fun.seidel.client.components.COAPClientComponent;

@ShellComponent
public class Connect {

    private final COAPClientComponent coapClientComponent;

    public Connect(COAPClientComponent coapClientComponent) {
        this.coapClientComponent = coapClientComponent;
    }

    @ShellMethod("Connect to a coap server.")
    public void connect(
            @ShellOption String url,
            @ShellOption String username
    ) throws ConnectorException, IOException {
        coapClientComponent.connect(url, username);
    }

    @ShellMethod("Info about your connection")
    @ShellMethodAvailability("authenticateAvailability")
    public CharSequence whoAmI() {
        return new AttributedStringBuilder()
                .append("Information about your connection\n", AttributedStyle.BOLD)
                .append("Connected Server: ")
                .append(coapClientComponent.get().getURI(), AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN))
                .append("\n")
                .append("Your username: ")
                .append(coapClientComponent.getUsername(), AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN));
    }

    private Availability authenticateAvailability() {
        return coapClientComponent.isConnected() ?
                Availability.available() : Availability.unavailable("you are not connected..");
    }
}
