package lk.raminsenanayake.globaltrade_logistics.web.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Inventory;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.InventoryPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.web.model.CreateInventoryRequest;
import lk.raminsenanayake.globaltrade_logistics.web.model.RestockRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryController {

    @Inject
    private InventoryPersistenceService inventoryPersistenceService;

    @GET
    public Response getInventoryAll() {
        List<Inventory> items = inventoryPersistenceService.findAll();
        return Response.ok(items).build();
    }

    @GET
    @Path("/{sku}")
    public Response getInventoryBySku(@PathParam("sku") String sku) {
        Optional<Inventory> item = inventoryPersistenceService.findBySku(sku);
        if (item.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Inventory item not found with SKU: " + sku))
                    .build();
        }
        return Response.ok(item.get()).build();
    }

    @GET
    @Path("/low-stock")
    public Response getLowStockItems() {
        List<Inventory> lowStock = inventoryPersistenceService.findBelowReorderThreshold();
        return Response.ok(lowStock).build();
    }

    @POST
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL"})
    public Response createInventoryItem(CreateInventoryRequest req) {
        if (req == null || req.getSku() == null || req.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "SKU and Name are mandatory."))
                    .build();
        }

        if (inventoryPersistenceService.findBySku(req.getSku()).isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Inventory item with SKU already exists: " + req.getSku()))
                    .build();
        }

        Inventory inventory = new Inventory(
                req.getSku(),
                req.getName(),
                req.getCategory(),
                req.getQty(),
                req.getReorderThreshold(),
                req.getUnitPrice(),
                req.getWarehouseLocation()
        );

        Inventory saved = inventoryPersistenceService.save(inventory);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @POST
    @Path("/{sku}/restock")
    @RolesAllowed({"ADMIN", "LOGISTIC_PERSONNEL"})
    public Response restockItem(@PathParam("sku") String sku, RestockRequest req) {
        if (req == null || req.getQuantity() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Restock quantity must be greater than zero."))
                    .build();
        }

        Optional<Inventory> opt = inventoryPersistenceService.findBySku(sku);
        if (opt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "SKU not found: " + sku))
                    .build();
        }

        inventoryPersistenceService.restock(sku, req.getQuantity());
        Inventory updated = inventoryPersistenceService.findBySku(sku).orElse(null);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response deleteInventoryItem(@PathParam("id") Long id) {
        inventoryPersistenceService.delete(id);
        return Response.noContent().build();
    }
}
