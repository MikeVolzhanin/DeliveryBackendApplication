package ru.volzhanin.deliverybackendapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.volzhanin.deliverybackendapplication.entity.RefreshToken;
import ru.volzhanin.deliverybackendapplication.entity.User;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(Long UserId);
    @Modifying
    @Query(value = "DELETE FROM refresh_token WHERE user_id = ?1", nativeQuery = true)
    void deleteByUserId(Long userId);
}