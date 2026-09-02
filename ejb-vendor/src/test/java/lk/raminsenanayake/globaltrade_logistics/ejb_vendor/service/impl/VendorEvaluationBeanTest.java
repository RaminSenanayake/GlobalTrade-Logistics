package lk.raminsenanayake.globaltrade_logistics.ejb_vendor.service.impl;

import lk.raminsenanayake.globaltrade_logistics.ejb_api.dto.VendorScorecard;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentStatus;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Vendor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.VendorComplianceStatus;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.VendorPersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorEvaluationBeanTest {

    @Mock
    private VendorPersistenceService vendorService;

    @Mock
    private ShipmentPersistenceService shipmentService;

    @Mock
    private AlertPersistenceService alertService;

    @InjectMocks
    private VendorEvaluationBean bean;

    @Test
    void registerVendor_Success() {
        Vendor vendor = new Vendor();
        vendor.setName("Apex Global Express");
        vendor.setCountry("USA");

        when(vendorService.save(any(Vendor.class))).thenAnswer(i -> i.getArgument(0));

        Vendor saved = bean.registerVendor(vendor);
        assertNotNull(saved);
        assertNotNull(saved.getVendorCode());
        assertEquals(VendorComplianceStatus.COMPLIANT, saved.getComplianceStatus());
    }

    @Test
    void evaluateVendor_HighPerformance_CalculatesCorrectScorecard() {
        Vendor vendor = new Vendor("VND-001", "FastCargo", "USA", "fast@cargo.com");
        when(vendorService.findByVendorCode("VND-001")).thenReturn(Optional.of(vendor));

        Shipment s1 = new Shipment();
        s1.setAssignedVendor("VND-001");
        s1.setStatus(ShipmentStatus.DELIVERED);

        Shipment s2 = new Shipment();
        s2.setAssignedVendor("VND-001");
        s2.setStatus(ShipmentStatus.DELIVERED);

        when(shipmentService.findAll()).thenReturn(List.of(s1, s2));

        VendorScorecard scorecard = bean.evaluateVendor("VND-001");
        assertNotNull(scorecard);
        assertEquals(100.0, scorecard.getOnTimeDeliveryRate());
        assertEquals(5.0, scorecard.getPerformanceRating());
        assertEquals(2, scorecard.getTotalShipments());
    }
}
