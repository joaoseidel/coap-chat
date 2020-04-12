package fun.seidel;

import static picocli.CommandLine.Command;

import org.eclipse.californium.core.CoapClient;

import fun.seidel.commands.Groups;
import picocli.CommandLine;

@Command()
public class Main implements Runnable {

    public final static String BASE_URL = "coap://localhost";
    public static final int JSON_FORMAT = 50;

    public static void main(String... args) {
        CoapClient client = new CoapClient(BASE_URL);

        new CommandLine(new Main())
                .addSubcommand(new Groups(client))
                .execute();
    }

    @Override
    public void run() {
        System.out.println("bem vindo!");
    }
}
