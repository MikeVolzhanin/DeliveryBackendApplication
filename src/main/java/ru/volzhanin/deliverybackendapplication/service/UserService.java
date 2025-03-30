package ru.volzhanin.deliverybackendapplication.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.volzhanin.deliverybackendapplication.entity.User;
import org.springframework.stereotype.Service;
import ru.volzhanin.deliverybackendapplication.repository.RefreshTokenRepository;
import ru.volzhanin.deliverybackendapplication.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public ResponseEntity<?> deleteUser(String email) {
        return userRepository.findByUsername(email)
                .map(user -> {
                    if (user.getRefreshToken() != null) {
                        refreshTokenRepository.delete(user.getRefreshToken());
                    }
                    userRepository.delete(user);
                    return new ResponseEntity<>(HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
}
