package lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.TradeComplianceViolationException;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.CustomsDeclaration;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlertSeverity;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.SupplyChainAlertType;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.AlertPersistenceService;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class RegulatoryComplianceInterceptor implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(RegulatoryComplianceInterceptor.class.getName());

    // Sanctioned country codes (ISO 3-letter alpha codes for trade compliance simulation)
    private static final Set<String> EMBARGOED_DESTINATIONS = new HashSet<>(Arrays.asList(
            "PRK", "IRN", "SYR", "CUB", "SDN"
    ));

    @Inject
    private AlertPersistenceService alertService;

    @AroundInvoke
    public Object enforceCompliance(InvocationContext context) throws Exception {
        Object[] params = context.getParameters();
        if (params != null) {
            for (Object param : params) {
                if (param instanceof Shipment) {
                    validateShipmentCompliance((Shipment) param);
                } else if (param instanceof CustomsDeclaration) {
                    validateCustomsCompliance((CustomsDeclaration) param);
                }
            }
        }
        return context.proceed();
    }

    private void validateShipmentCompliance(Shipment shipment) {
        String dest = shipment.getDestinationCountry();
        if (dest != null && EMBARGOED_DESTINATIONS.contains(dest.toUpperCase().trim())) {
            String msg = "Trade compliance violation: Destination country '" + dest + "' is under international embargo.";
            LOGGER.severe(msg);
            if (alertService != null) {
                alertService.recordAlert(
                        SupplyChainAlertType.TRADE_SANCTION_DETECTED,
                        SupplyChainAlertSeverity.CRITICAL,
                        "Embargo Violation Attempt",
                        msg,
                        shipment.getTrackingNumber()
                );
            }
            throw new TradeComplianceViolationException(msg);
        }

        if (shipment.getDeclaredValueUSD() > 100000.0) {
            LOGGER.warning("High-value shipment flagged for regulatory review: " + shipment.getTrackingNumber());
            if (alertService != null) {
                alertService.recordAlert(
                        SupplyChainAlertType.CUSTOMS_HOLD,
                        SupplyChainAlertSeverity.HIGH,
                        "High-Value Shipment Review Required",
                        "Shipment " + shipment.getTrackingNumber() + " declared value exceeds $100,000 (Value: $" + shipment.getDeclaredValueUSD() + ")",
                        shipment.getTrackingNumber()
                );
            }
        }
    }

    private void validateCustomsCompliance(CustomsDeclaration declaration) {
        String dest = declaration.getDestinationCountry();
        if (dest != null && EMBARGOED_DESTINATIONS.contains(dest.toUpperCase().trim())) {
            String msg = "Trade compliance violation: Customs declaration for embargoed country '" + dest + "' rejected.";
            LOGGER.severe(msg);
            if (alertService != null) {
                alertService.recordAlert(
                        SupplyChainAlertType.TRADE_SANCTION_DETECTED,
                        SupplyChainAlertSeverity.CRITICAL,
                        "Customs Embargo Rejection",
                        msg,
                        declaration.getDeclarationNumber()
                );
            }
            throw new TradeComplianceViolationException(msg);
        }
    }
}
