package lk.raminsenanayake.globaltrade_logistics.ejb_api.customs;

import jakarta.ejb.Local;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.CustomsDeclaration;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.CustomsDeclarationStatus;

import java.util.List;

@Local
public interface CustomsComplianceServiceLocal {

    CustomsDeclaration submitDeclaration(CustomsDeclaration declaration);

    void reviewDeclaration(String declarationNumber, CustomsDeclarationStatus status, String reviewedBy, String notes);

    boolean checkCompliance(String trackingNumber);

    List<CustomsDeclaration> getPendingDeclarations();

    List<CustomsDeclaration> getApproachingDeadlineDeclarations(int hoursAhead);
}
