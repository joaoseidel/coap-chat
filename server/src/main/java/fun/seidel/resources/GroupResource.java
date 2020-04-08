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

public class GroupResource extends CoapResource {

    public static final int JSON_CONTENT_FORM = 50;

    public GroupResource(String name) {
        super(name);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        if (exchange.getRequestOptions().getUriQuery().isEmpty()) {
            exchange.respond(new Gson().toJson(Groups.findAll()));
        }

        OptionSet requestOptions = exchange.getRequestOptions();
        requestOptions.getUriQuery().forEach(queryParam -> {
            Groups.find(UUID.fromString(queryParam)).ifPresent(
                    group -> {
                        Response response = new Response(CONTENT);
                        response.setPayload(new Gson().toJson(group));
                        response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));

                        exchange.respond(response);
                    }
            );
        });
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();

        OptionSet requestOptions = exchange.getRequestOptions();
        requestOptions.getUriQuery().forEach(queryParam -> {
            Groups.find(UUID.fromString(queryParam)).ifPresent(group -> {
                Message message = new Gson().fromJson(exchange.getRequestText(), Message.class)
                        .setUuid(UUID.randomUUID())
                        .setDestination(group.getUuid())
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

