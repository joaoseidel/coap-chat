package fun.seidel.resources;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.BAD_REQUEST;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CONTENT;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.DELETED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.FORBIDDEN;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.NOT_FOUND;

import java.util.Objects;
import java.util.UUID;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import fun.seidel.cache.Groups;
import fun.seidel.model.Group;

public class GroupResource extends CoapResource {
    private static final int JSON_CONTENT_FORM = 50;

    public GroupResource(String name) {
        super(name);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        if (exchange.getRequestOptions().getUriQuery().isEmpty()) {
            Response response = new Response(CONTENT);
            response.setPayload(new Gson().toJson(Groups.findAll()));
            response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));
            exchange.respond(response);
        }

        OptionSet requestOptions = exchange.getRequestOptions();
        requestOptions.getUriQuery().forEach(queryParam -> Groups.find(UUID.fromString(queryParam)).ifPresent(
                group -> {
                    Response response = new Response(CONTENT);
                    response.setPayload(new Gson().toJson(group));
                    response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));

                    exchange.respond(response);
                }
        ));

        exchange.respond(NOT_FOUND);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();

        if (Objects.nonNull(exchange.getRequestText())) {
            Group group = new Gson().fromJson(exchange.getRequestText(), Group.class)
                    .setUuid(UUID.randomUUID());

            Groups.add(group);

            Response response = new Response(CREATED);
            response.setPayload(new Gson().toJson(group));
            response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));
            exchange.respond(response);
        }

        exchange.respond(BAD_REQUEST);
    }

    @Override
    public void handleDELETE(CoapExchange exchange) {
        exchange.accept();

        OptionSet requestOptions = exchange.getRequestOptions();
        requestOptions.getUriQuery().forEach(queryParam -> Groups.find(UUID.fromString(queryParam)).ifPresent(group -> {
            if (group.isDefaultGroup()) {
                exchange.respond(FORBIDDEN);
                return;
            }

            Groups.remove(UUID.fromString(queryParam));
            exchange.respond(DELETED);
        }));

        exchange.respond(NOT_FOUND);
    }
}
