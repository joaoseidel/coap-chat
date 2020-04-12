package fun.seidel;

import static picocli.CommandLine.Command;

import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapClient;

import fun.seidel.commands.Groups;
import fun.seidel.model.User;
import picocli.CommandLine;

@Command
public class Client implements Runnable {

    public static final int JSON_FORMAT = 50;
    public static String BASE_URL = "coap://";
    public static User currentUser;

    public static void main(String... args) {
        Logger.getRootLogger().setLevel(Level.OFF);

        if (args.length == 0 || args[0].isEmpty()) {
            System.out.println("Usage: client [username:server_url] --options");
            return;
        }

        String[] argument = args[0].split(":");
        Client.BASE_URL += argument[1];

        CoapClient client = new CoapClient(Client.BASE_URL);

        Client.currentUser = new User()
                .setUsername(argument[0]);

        new CommandLine(new Client())
                .addSubcommand(new CommandLine(new Groups(client)))
                .execute(Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public void run() {
        System.out.println("Basics commands:");
        System.out.println("\t(g)roups");
        System.out.println("\t(p)rivate");
    }
}
