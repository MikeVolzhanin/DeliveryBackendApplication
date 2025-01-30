package ru.volzhanin.deliverybackendapplication.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.volzhanin.deliverybackendapplication.entity.RefreshToken;
import ru.volzhanin.deliverybackendapplication.entity.User;
import ru.volzhanin.deliverybackendapplication.repository.RefreshTokenRepository;
import ru.volzhanin.deliverybackendapplication.repository.UserRepository;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${refresh-token.duration}")
    private Duration refreshTokenDurationMin;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        User user = userRepository.findById(userId).get();

        if (findByUserId(user.getId()).isPresent()) {
            refreshTokenRepository.deleteByUserId(userId);
        }

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + refreshTokenDurationMin.toMillis());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expiredDate);
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public boolean verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(new Date()) < 0) {
            refreshTokenRepository.delete(token);
            return false;
        }
        return true;
    }

    public Optional<RefreshToken> findByUserId(Long id) {
        return refreshTokenRepository.findByUserId(id);
    }
}