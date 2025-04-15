package ru.volzhanin.deliverybackendapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.volzhanin.deliverybackendapplication.entity.UserShift;

@Repository
public interface UserShiftRepository extends JpaRepository<UserShift, Long> {
}
