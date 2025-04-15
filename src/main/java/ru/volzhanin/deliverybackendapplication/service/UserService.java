package ru.volzhanin.deliverybackendapplication.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.volzhanin.deliverybackendapplication.dto.UserDto;
import ru.volzhanin.deliverybackendapplication.entity.User;
import org.springframework.stereotype.Service;
import ru.volzhanin.deliverybackendapplication.repository.RefreshTokenRepository;
import ru.volzhanin.deliverybackendapplication.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Transactional
    public ResponseEntity<?> updateUser(UserDto userDto) {
        return userRepository.findByUsername(userDto.getEmail())
                .map(user -> {
                    user.setFirstName(userDto.getFirstName());
                    user.setMiddleName(userDto.getMiddleName());
                    user.setSurname(userDto.getSurname());
                    user.setPhoneNumber(userDto.getPhone());
                    user.setUsername(userDto.getEmail());
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() ->
                        ResponseEntity
                                .badRequest()
                                .body(Map.of("error", "Пользователь не найден"))
                );
    }

}
