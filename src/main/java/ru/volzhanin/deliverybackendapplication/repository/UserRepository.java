package ru.volzhanin.deliverybackendapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.volzhanin.deliverybackendapplication.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String email);

    Optional<User> findByVerificationCode(String verificationCode);
}
