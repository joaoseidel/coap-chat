package fun.seidel.resources;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CONTENT;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.NOT_FOUND;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import fun.seidel.cache.PrivateMessages;
import fun.seidel.cache.Users;
import fun.seidel.model.Message;

public class PrivateMessageResource extends CoapResource {

    private static final int JSON_CONTENT_FORM = 50;

    public PrivateMessageResource(String name) {
        super(name);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        List<Message> messages = PrivateMessages.findAll();

        if (Objects.nonNull(messages) && !messages.isEmpty()) {
            Response response = new Response(CONTENT);
            response.setPayload(new Gson().toJson(messages));
            response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));

            exchange.respond(response);
        }

        exchange.respond(NOT_FOUND);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();

        OptionSet requestOptions = exchange.getRequestOptions();
        requestOptions.getUriQuery().stream()
                .filter(Users::exists)
                .forEach(username -> {
                    Message message = new Gson().fromJson(exchange.getRequestText(), Message.class)
                            .setUuid(UUID.randomUUID())
                            .setDestination(username)
                            .setCreationDate(new Date());
                    if (Objects.nonNull(message.getSender())) {
                        PrivateMessages.add(message);

                        Response response = new Response(CREATED);
                        response.setPayload(new Gson().toJson(message));
                        response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));
                        exchange.respond(response);
                    }
                });

        exchange.respond(NOT_FOUND);
    }
}
