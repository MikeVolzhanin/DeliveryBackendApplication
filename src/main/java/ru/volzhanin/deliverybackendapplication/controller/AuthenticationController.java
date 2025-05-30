package ru.volzhanin.deliverybackendapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.volzhanin.deliverybackendapplication.dto.*;
import ru.volzhanin.deliverybackendapplication.entity.RefreshToken;
import ru.volzhanin.deliverybackendapplication.entity.User;
import ru.volzhanin.deliverybackendapplication.service.AuthenticationService;
import ru.volzhanin.deliverybackendapplication.service.JwtService;
import ru.volzhanin.deliverybackendapplication.service.RefreshTokenService;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Tag(
        name = "Аутентификация пользователя"
)
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
    }

    @Operation(
            summary = "Регистрация нового пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные || Пользователь уже зарегистрирован"),
    })
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        return authenticationService.signup(registerUserDto);
    }

    @Operation(
            summary = "Вход пользователя в систему"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход, возвращает access и refersh токены "),
            @ApiResponse(responseCode = "401", description = "Аккаунт не верифицирован по почте"),
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto) {
        return authenticationService.authenticate(loginUserDto);
    }

    @Operation(
            summary = "Верификация аккаунта пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аккаунт успешно верифицирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных или некорректный код подтверждения")
    })
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        return authenticationService.verifyUser(verifyUserDto);
    }

    @Operation(
            summary = "Повторная отправка кода верификации"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Код верификации успешно отправлен"),
            @ApiResponse(responseCode = "400", description = "Некорректный email или ошибка обработки запроса")
    })
    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        return authenticationService.resendVerificationCode(email);
    }

    @Operation(
            summary = "Обновление токена",
            description = "Позволяет обновить JWT токен, если он истекает, предоставив refresh-токен."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Недействительный refresh-токен"),
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String stringRequest = request.getRefreshToken();

        Optional<RefreshToken> refreshToken = refreshTokenService.findByToken(stringRequest);

        if(refreshToken.isEmpty()) return new ResponseEntity<>("This refresh token doesn't exist", HttpStatus.BAD_REQUEST);

        if (refreshTokenService.verifyExpiration(refreshToken.get())) {
            User user = refreshToken.get().getUser();

            String newToken = jwtService.generateToken(
                    authenticationService.loadUserByUsername(user.getUsername())
            );

            String newRefreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

            return ResponseEntity.ok(new TokenDto(newToken, newRefreshToken));
        }

        return new ResponseEntity<>("Expired refresh token", HttpStatus.BAD_REQUEST);
    }
}

