package fun.seidel.resources;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.BAD_REQUEST;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CONTENT;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;

import java.util.Objects;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import fun.seidel.cache.Users;

public class UsersResource extends CoapResource {
    private static final int JSON_CONTENT_FORM = 50;

    public UsersResource(String name) {
        super(name);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        Response response = new Response(CONTENT);
        response.setPayload(new Gson().toJson(Users.findAll()));
        response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));
        exchange.respond(response);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();

        if (Objects.nonNull(exchange.getRequestText())) {
            String user = new Gson().fromJson(exchange.getRequestText(), String.class);

            Users.add(user);

            Response response = new Response(CREATED);
            response.setPayload(new Gson().toJson(user));
            response.setOptions(new OptionSet().setContentFormat(JSON_CONTENT_FORM));
            exchange.respond(response);
            return;
        }

        exchange.respond(BAD_REQUEST);
    }
}
