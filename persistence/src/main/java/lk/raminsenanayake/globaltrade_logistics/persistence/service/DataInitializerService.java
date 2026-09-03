package lk.raminsenanayake.globaltrade_logistics.persistence.service;

import jakarta.ejb.Local;

@Local
public interface DataInitializerService {
    void initializeDefaultData();
}
