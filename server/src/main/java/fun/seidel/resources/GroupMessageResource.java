package fun.seidel.resources;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CONTENT;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.NOT_FOUND;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import fun.seidel.cache.Groups;
import fun.seidel.model.Message;

public class GroupMessageResource extends CoapResource {

    private static final int JSON_CONTENT_FORM = 50;

    public GroupMessageResource(String name) {
        super(name);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        OptionSet requestOptions = exchange.getRequestOptions();
        requestOptions.getUriQuery().forEach(queryParam -> {
            Groups.find(UUID.fromString(queryParam)).ifPresent(
                    group -> {
                        Response response = new Response(CONTENT);
                        response.setPayload(new Gson().toJson(group.getMessages()));
                        response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));

                        exchange.respond(response);
                    }
            );
        });

        exchange.respond(NOT_FOUND);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();

        OptionSet requestOptions = exchange.getRequestOptions();
        requestOptions.getUriQuery().forEach(queryParam -> {
            Groups.find(UUID.fromString(queryParam)).ifPresent(group -> {
                Message message = new Gson().fromJson(exchange.getRequestText(), Message.class)
                        .setUuid(UUID.randomUUID())
                        .setDestination(group.getUuid().toString())
                        .setCreationDate(new Date());

                if (Objects.nonNull(message.getSender())) {
                    Groups.addMessage(group.getUuid(), message);

                    Response response = new Response(CREATED);
                    response.setPayload(new Gson().toJson(Groups.find(group.getUuid())));
                    response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));
                    exchange.respond(response);
                }
            });
        });

        exchange.respond(NOT_FOUND);
    }
}

