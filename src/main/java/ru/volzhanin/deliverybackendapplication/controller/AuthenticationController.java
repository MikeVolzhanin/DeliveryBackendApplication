package ru.volzhanin.deliverybackendapplication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.volzhanin.deliverybackendapplication.dto.*;
import ru.volzhanin.deliverybackendapplication.entity.RefreshToken;
import ru.volzhanin.deliverybackendapplication.entity.User;
import ru.volzhanin.deliverybackendapplication.exceptions.TokenRefreshException;
import ru.volzhanin.deliverybackendapplication.response.LoginResponse;
import ru.volzhanin.deliverybackendapplication.service.AuthenticationService;
import ru.volzhanin.deliverybackendapplication.service.JwtService;
import ru.volzhanin.deliverybackendapplication.service.RefreshTokenService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenRefreshResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser.getId());
        return ResponseEntity.ok(new TokenRefreshResponse(jwtToken, refreshToken.getToken()));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String stringRequest = request.getRefreshToken();

        return refreshTokenService.findByToken(stringRequest)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(authenticationService.loadUserByUsername(user.getUsername()));
                    return ResponseEntity.ok(new TokenRefreshResponse(token, refreshTokenService.createRefreshToken(user.getId()).getToken()));
                })
                .orElseThrow(() -> new TokenRefreshException(stringRequest,
                        "Refresh token is not in database!"));
    }
}

