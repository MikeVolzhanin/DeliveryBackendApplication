package ru.volzhanin.deliverybackendapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.volzhanin.deliverybackendapplication.dto.*;
import ru.volzhanin.deliverybackendapplication.entity.RefreshToken;
import ru.volzhanin.deliverybackendapplication.exceptions.TokenRefreshException;
import ru.volzhanin.deliverybackendapplication.service.AuthenticationService;
import ru.volzhanin.deliverybackendapplication.service.JwtService;
import ru.volzhanin.deliverybackendapplication.service.RefreshTokenService;

@RestController
@RequestMapping("/auth")
@Tag(
        name = "Аутентификация пользователя",
        description = "Контроллер предоставляет эндпоинты для управления процессом аутентификации пользователей, включая регистрацию, вход в систему, выход и обновление токенов."
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
            summary = "Регистрация нового пользователя",
            description = "Создает новую учетную запись пользователя на основе предоставленных данных (например, имени, почты и пароля)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные || Пользователь уже зарегистрирован"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        return authenticationService.signup(registerUserDto);
    }

    @Operation(
            summary = "Вход пользователя в систему",
            description = "Позволяет пользователю войти в систему, предоставив корректные учетные данные (почта и пароль)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход, возвращает access и refersh токены "),
            @ApiResponse(responseCode = "401", description = "Некорректные учетные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto) {
        return authenticationService.authenticate(loginUserDto);
    }

    @Operation(
            summary = "Верификация аккаунта пользователя",
            description = "Эндпоинт проверяет код подтверждения, отправленный на электронную почту пользователя, для завершения процесса верификации аккаунта."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аккаунт успешно верифицирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных или некорректный код подтверждения",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
         return authenticationService.verifyUser(verifyUserDto);
    }

    @Operation(
            summary = "Повторная отправка кода верификации",
            description = "Эндпоинт отправляет новый код верификации на указанный email пользователя, если предыдущий код истек или был утерян."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Код верификации успешно отправлен"),
            @ApiResponse(responseCode = "400", description = "Некорректный email или ошибка обработки запроса",
                    content = @Content(mediaType = "application/json"))
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
            @ApiResponse(responseCode = "401", description = "Недействительный refresh-токен"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String stringRequest = request.getRefreshToken();

        return refreshTokenService.findByToken(stringRequest)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(authenticationService.loadUserByUsername(user.getUsername()));
                    return ResponseEntity.ok(new TokenDto(token, refreshTokenService.createRefreshToken(user.getId()).getToken()));
                })
                .orElseThrow(() -> new TokenRefreshException(stringRequest,
                        "Refresh token is not in database!"));
    }
}

