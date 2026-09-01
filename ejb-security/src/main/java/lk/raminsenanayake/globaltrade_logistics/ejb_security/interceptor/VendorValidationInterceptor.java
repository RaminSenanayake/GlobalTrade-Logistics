package lk.raminsenanayake.globaltrade_logistics.ejb_security.interceptor;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import lk.raminsenanayake.globaltrade_logistics.ejb_api.exception.VendorComplianceException;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Vendor;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.VendorComplianceStatus;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.VendorPersistenceService;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Logger;

public class VendorValidationInterceptor implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(VendorValidationInterceptor.class.getName());

    @Inject
    private VendorPersistenceService vendorService;

    @AroundInvoke
    public Object validateVendorStatus(InvocationContext context) throws Exception {
        Object[] params = context.getParameters();
        if (params != null) {
            for (Object param : params) {
                if (param instanceof String) {
                    String str = (String) param;
                    if (str.startsWith("VND-")) {
                        checkVendorEligibility(str);
                    }
                } else if (param instanceof Vendor) {
                    Vendor v = (Vendor) param;
                    if (v.getVendorCode() != null && v.getVendorCode().startsWith("VND-")) {
                        checkVendorEligibility(v.getVendorCode());
                    }
                }
            }
        }
        return context.proceed();
    }

    private void checkVendorEligibility(String vendorCode) {
        if (vendorService == null) {
            return;
        }
        Optional<Vendor> optVendor = vendorService.findByVendorCode(vendorCode);
        if (optVendor.isPresent()) {
            Vendor v = optVendor.get();
            if (v.getComplianceStatus() == VendorComplianceStatus.SUSPENDED) {
                LOGGER.warning("Blocked operation for suspended vendor: " + vendorCode);
                throw new VendorComplianceException("Vendor " + vendorCode + " is SUSPENDED and cannot perform transactions.");
            }
        }
    }
}
