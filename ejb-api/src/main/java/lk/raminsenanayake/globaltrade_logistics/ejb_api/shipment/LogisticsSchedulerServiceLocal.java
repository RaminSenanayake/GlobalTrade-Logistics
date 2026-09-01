package lk.raminsenanayake.globaltrade_logistics.ejb_api.shipment;

import jakarta.ejb.Local;

@Local
public interface LogisticsSchedulerServiceLocal {

    void runScheduledDelayDetection();

    void runScheduledCustomsDeadlineCheck();

    void runScheduledInventoryRestockCheck();
}
