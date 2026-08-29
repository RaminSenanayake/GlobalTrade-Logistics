package lk.raminsenanayake.globaltrade_logistics.web.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/inventory")
public class InventoryController {

    @GET
    public String getInventoryAll(){
        return "getInventoryAll"; //TODO replace this stub to something useful
    }
}
