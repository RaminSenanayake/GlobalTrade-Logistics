package lk.raminsenanayake.globaltrade_logistics.ejb_customs.service;

import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.ShipmentNotFoundException;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.CustomsDeclaration;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.CustomsDeclarationStatus;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.CustomsPersistenceService;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomsComplianceBeanTest {

    @Mock
    private CustomsPersistenceService customsService;

    @Mock
    private ShipmentPersistenceService shipmentService;

    @Mock
    private AlertPersistenceService alertService;

    @InjectMocks
    private CustomsComplianceBean bean;

    @Test
    void submitDeclaration_Success() {
        CustomsDeclaration decl = new CustomsDeclaration();
        decl.setTrackingNumber("TRK-123");
        decl.setDestinationCountry("GBR");

        when(shipmentService.findByTrackingNumber("TRK-123")).thenReturn(Optional.of(new Shipment()));
        when(customsService.save(any(CustomsDeclaration.class))).thenAnswer(i -> i.getArgument(0));

        CustomsDeclaration result = bean.submitDeclaration(decl);
        assertNotNull(result);
        assertNotNull(result.getDeclarationNumber());
        assertEquals(CustomsDeclarationStatus.SUBMITTED, result.getStatus());
        verify(customsService).save(decl);
    }

    @Test
    void submitDeclaration_ShipmentNotFound_ThrowsException() {
        CustomsDeclaration decl = new CustomsDeclaration();
        decl.setTrackingNumber("TRK-NOT-FOUND");

        when(shipmentService.findByTrackingNumber("TRK-NOT-FOUND")).thenReturn(Optional.empty());

        assertThrows(ShipmentNotFoundException.class, () -> bean.submitDeclaration(decl));
    }

    @Test
    void reviewDeclaration_Approve_Success() {
        CustomsDeclaration decl = new CustomsDeclaration();
        decl.setDeclarationNumber("DEC-001");
        decl.setTrackingNumber("TRK-001");
        decl.setStatus(CustomsDeclarationStatus.SUBMITTED);

        when(customsService.findByDeclarationNumber("DEC-001")).thenReturn(Optional.of(decl));

        bean.reviewDeclaration("DEC-001", CustomsDeclarationStatus.APPROVED, "OFFICER_BOB", "All Clear");
        verify(customsService).updateStatus("DEC-001", CustomsDeclarationStatus.APPROVED, "OFFICER_BOB", "All Clear");
    }
}
