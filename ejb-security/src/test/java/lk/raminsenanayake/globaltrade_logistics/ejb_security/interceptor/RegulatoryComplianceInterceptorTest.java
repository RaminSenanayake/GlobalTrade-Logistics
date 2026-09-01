package lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor;

import jakarta.interceptor.InvocationContext;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.TradeComplianceViolationException;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegulatoryComplianceInterceptorTest {

    @Mock
    private AlertPersistenceService alertService;

    @Mock
    private InvocationContext invocationContext;

    @InjectMocks
    private RegulatoryComplianceInterceptor interceptor;

    @Test
    void enforceCompliance_EmbargoedDestination_ThrowsException() {
        Shipment shipment = new Shipment();
        shipment.setTrackingNumber("TRK-EMBARGO");
        shipment.setDestinationCountry("PRK");

        when(invocationContext.getParameters()).thenReturn(new Object[]{shipment});

        assertThrows(TradeComplianceViolationException.class, () -> interceptor.enforceCompliance(invocationContext));
    }

    @Test
    void enforceCompliance_ValidDestination_Proceeds() throws Exception {
        Shipment shipment = new Shipment();
        shipment.setTrackingNumber("TRK-VALID");
        shipment.setDestinationCountry("GBR");

        when(invocationContext.getParameters()).thenReturn(new Object[]{shipment});
        when(invocationContext.proceed()).thenReturn("SUCCESS");

        Object result = interceptor.enforceCompliance(invocationContext);
        assertEquals("SUCCESS", result);
        verify(invocationContext).proceed();
    }
}
