package lk.raminsenanayake.globaltrade_logistics.persistence.entity;

public enum ShipmentStatus {
    CREATED,
    PENDING_CLEARANCE,
    CUSTOMS_HOLD,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    DELAYED,
    CANCELLED
}
