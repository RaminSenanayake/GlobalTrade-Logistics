package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.*;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.*;

@Stateless
public class DataInitializerServiceImpl implements DataInitializerService {

    @Inject
    private UserPersistenceService userPersistenceService;

    @Inject
    private InventoryPersistenceService inventoryService;

    @Inject
    private VendorPersistenceService vendorService;

    @Override
    public void initializeDefaultData() {
        // Seed default users if admin does not exist
        if (!userPersistenceService.existsByUsername("admin")) {
            userPersistenceService.createUser("admin", "Admin@123", UserRole.ADMIN);
            userPersistenceService.createUser("logistics_mgr", "Logistics@123", UserRole.LOGISTIC_PERSONNEL);
            userPersistenceService.createUser("customs_agent", "Customs@123", UserRole.CUSTOM_OFFICIAL);
            userPersistenceService.createUser("vendor_rep", "Vendor@123", UserRole.VENDOR);
            userPersistenceService.createUser("customer1", "Customer@123", UserRole.CUSTOMER);
        }

        // Seed default vendors
        if (vendorService.findByVendorCode("VND-001").isEmpty()) {
            vendorService.save(new Vendor("VND-001", "Pacific Cargo Lines", "SGP", "contact@pacificcargo.com"));
            vendorService.save(new Vendor("VND-002", "Atlantic Freight Solutions", "DEU", "support@atlanticfreight.de"));
            vendorService.save(new Vendor("VND-003", "TransGlobal Express", "USA", "ops@transglobal.com"));
        }

        // Seed default inventory items
        if (inventoryService.findBySku("SKU-MED-001").isEmpty()) {
            inventoryService.save(new Inventory("SKU-MED-001", "Cold Chain Insulin Vials", "Pharmaceuticals", 500, 100, 45.50, "WH-COLD-01"));
            inventoryService.save(new Inventory("SKU-ELEC-002", "Industrial Sensor Nodes", "Electronics", 1200, 250, 89.00, "WH-MAIN-02"));
            inventoryService.save(new Inventory("SKU-AUTO-003", "EV Lithium Battery Packs", "Automotive", 45, 50, 1200.00, "WH-HAZMAT-03")); // Below threshold to trigger replenishment!
        }
    }
}
