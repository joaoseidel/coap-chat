package fun.seidel.resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class GroupManageResource extends CoapResource {
    public GroupManageResource(String name) {
        super(name);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        super.handleGET(exchange);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        super.handlePOST(exchange);
    }

    @Override
    public void handleDELETE(CoapExchange exchange) {
        super.handleDELETE(exchange);
    }
}
