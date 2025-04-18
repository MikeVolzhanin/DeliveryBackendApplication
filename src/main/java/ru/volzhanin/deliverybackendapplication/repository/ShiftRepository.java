package ru.volzhanin.deliverybackendapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.volzhanin.deliverybackendapplication.entity.Shift;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
