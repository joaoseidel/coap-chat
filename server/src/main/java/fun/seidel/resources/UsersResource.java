package fun.seidel.resources;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.BAD_REQUEST;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CONTENT;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.DELETED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.NOT_FOUND;

import java.util.Objects;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import fun.seidel.cache.Users;
import fun.seidel.model.User;

public class UsersResource extends CoapResource {
    private static final int JSON_CONTENT_FORM = 50;

    public UsersResource(String name) {
        super(name);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        if (exchange.getRequestOptions().getUriQuery().isEmpty()) {
            Response response = new Response(CONTENT);
            response.setPayload(new Gson().toJson(Users.findAll()));
            response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));
            exchange.respond(response);
        }

        OptionSet requestOptions = exchange.getRequestOptions();
        requestOptions.getUriQuery().forEach(queryParam -> Users.find(queryParam).ifPresent(
                user -> {
                    Response response = new Response(CONTENT);
                    response.setPayload(new Gson().toJson(user));
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
            User user = new Gson().fromJson(exchange.getRequestText(), User.class)
                    .setPrivateMessaging(null);

            Users.add(user);

            Response response = new Response(CREATED);
            response.setPayload(new Gson().toJson(user));
            response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));
            exchange.respond(response);
        }

        exchange.respond(BAD_REQUEST);
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        exchange.accept();

        OptionSet requestOptions = exchange.getRequestOptions();
        requestOptions.getUriQuery().forEach(queryParam -> Users.find(queryParam).ifPresent(user -> {
            User updateUser = new Gson().fromJson(exchange.getRequestText(), User.class)
                    .setPrivateMessaging(null);

            Users.remove(queryParam);
            Users.add(updateUser);

            Response response = new Response(CREATED);
            response.setPayload(new Gson().toJson(updateUser));
            response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));
            exchange.respond(response);
        }));

        exchange.respond(NOT_FOUND);
    }

    @Override
    public void handleDELETE(CoapExchange exchange) {
        exchange.accept();

        OptionSet requestOptions = exchange.getRequestOptions();
        requestOptions.getUriQuery().forEach(queryParam -> Users.find(queryParam).ifPresent(user -> {
            Users.remove(queryParam);
            exchange.respond(DELETED);
        }));

        exchange.respond(NOT_FOUND);
    }
}
