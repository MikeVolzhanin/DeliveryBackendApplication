package ru.volzhanin.deliverybackendapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.volzhanin.deliverybackendapplication.entity.VehicleDetails;

@Repository
public interface VehicleDetailsRepository extends JpaRepository<VehicleDetails, Long> {
}
