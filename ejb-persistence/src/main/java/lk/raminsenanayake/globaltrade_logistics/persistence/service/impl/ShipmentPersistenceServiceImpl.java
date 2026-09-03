package lk.raminsenanayake.globaltrade_logistics.persistence.service.impl;

import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonPatch;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.Shipment;
import lk.raminsenanayake.globaltrade_logistics.persistence.entity.ShipmentStatus;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.ShipmentPersistenceService;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
public class ShipmentPersistenceServiceImpl implements ShipmentPersistenceService {

    @PersistenceContext(unitName = "globalTrade-logistics")
    private EntityManager em;

    @Override
    public Shipment save(Shipment shipment) {
        if (shipment.getId() == null) {
            em.persist(shipment);
            return shipment;
        } else {
            return em.merge(shipment);
        }
    }

    @Override
    public Optional<Shipment> findById(Long id) {
        return Optional.ofNullable(em.find(Shipment.class, id));
    }

    @Override
    public Optional<Shipment> findByTrackingNumber(String trackingNumber) {
        try {
            Shipment shipment = em.createQuery(
                            "SELECT s FROM Shipment s LEFT JOIN FETCH s.items WHERE s.trackingNumber = :tn", Shipment.class)
                    .setParameter("tn", trackingNumber)
                    .getSingleResult();
            return Optional.of(shipment);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Shipment> findAll() {
        return em.createQuery("SELECT DISTINCT s FROM Shipment s LEFT JOIN FETCH s.items ORDER BY s.createdAt DESC", Shipment.class)
                .getResultList();
    }

    @Override
    public List<Shipment> findBySender(String senderUsername) {
        return em.createQuery("SELECT DISTINCT s FROM Shipment s LEFT JOIN FETCH s.items WHERE s.senderUsername = :sender ORDER BY s.createdAt DESC", Shipment.class)
                .setParameter("sender", senderUsername)
                .getResultList();
    }

    @Override
    public List<Shipment> findByStatus(ShipmentStatus status) {
        return em.createQuery("SELECT DISTINCT s FROM Shipment s LEFT JOIN FETCH s.items WHERE s.status = :status", Shipment.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Shipment> findPotentialDelays(LocalDateTime thresholdTime) {
        return em.createQuery("SELECT s FROM Shipment s WHERE s.status IN (:s1, :s2) AND s.estimatedDelivery < :thresh", Shipment.class)
                .setParameter("s1", ShipmentStatus.IN_TRANSIT)
                .setParameter("s2", ShipmentStatus.PENDING_CLEARANCE)
                .setParameter("thresh", thresholdTime)
                .getResultList();
    }

    @Override
    public void update(Long id, JsonPatch jsonPatch) {
        findById(id).ifPresent(s -> {
            try (Jsonb jsonb = JsonbBuilder.create()) {
                String json = jsonb.toJson(s);
                JsonObject batchShipment = Json.createReader(new StringReader(json)).readObject();
                JsonObject updatedShipmentJson = jsonPatch.apply(batchShipment);
                Shipment updatedShipment = jsonb.fromJson(updatedShipmentJson.toString(), Shipment.class);
                em.merge(updatedShipment);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void updateStatus(String trackingNumber, ShipmentStatus newStatus) {
        findByTrackingNumber(trackingNumber).ifPresent(s -> {
            s.setStatus(newStatus);
            if (newStatus == ShipmentStatus.DELIVERED) {
                s.setActualDelivery(LocalDateTime.now());
            }
            em.merge(s);
        });
    }

    @Override
    public void delete(Long id) {
        Shipment shipment = em.find(Shipment.class, id);
        if (shipment != null) {
            em.remove(shipment);
        }
    }
}
