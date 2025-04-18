package ru.volzhanin.deliverybackendapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.volzhanin.deliverybackendapplication.entity.UserShift;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserShiftRepository extends JpaRepository<UserShift, Long> {
    Optional<UserShift> findByUserIdAndShiftId(Long userId, Long shiftId);
    boolean existsByUserIdAndShiftId(Long userId, Long shiftId);
    List<UserShift> findByUserIdAndCanceledAtIsNull(Long userId);
}
