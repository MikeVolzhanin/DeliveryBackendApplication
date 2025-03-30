package ru.volzhanin.deliverybackendapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.volzhanin.deliverybackendapplication.entity.Shift;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
