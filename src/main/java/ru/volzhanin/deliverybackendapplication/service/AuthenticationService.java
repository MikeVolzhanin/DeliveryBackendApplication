package ru.volzhanin.deliverybackendapplication.service;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.volzhanin.deliverybackendapplication.dto.LoginUserDto;
import ru.volzhanin.deliverybackendapplication.dto.RegisterUserDto;
import ru.volzhanin.deliverybackendapplication.dto.TokenDto;
import ru.volzhanin.deliverybackendapplication.dto.VerifyUserDto;
import ru.volzhanin.deliverybackendapplication.entity.RefreshToken;
import ru.volzhanin.deliverybackendapplication.repository.UserRepository;
import ru.volzhanin.deliverybackendapplication.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final DefaultEmailService emailService;
    private final TemplateEngine templateEngine;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    public ResponseEntity<?> signup(RegisterUserDto input) {
        User user = new User(
                input.getEmail(),
                input.getSurname(),
                input.getMiddleName(),
                input.getFirstName(),
                input.getPhoneNumber(),
                passwordEncoder.encode(input.getPassword())
        );

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);

        if (userRepository.findByUsername(user.getUsername()).isPresent() || userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent())
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);

        sendVerificationEmail(user);

        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> authenticate(LoginUserDto input) {
        User user = userRepository.findByUsername(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            return new ResponseEntity<>("Account not verified. Please verify your account", HttpStatus.UNAUTHORIZED);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new ResponseEntity<>(new TokenDto(accessToken, refreshToken.getToken()), HttpStatus.OK);
    }

    public ResponseEntity<?> verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userRepository.findByUsername(input.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>("Verification code has expired", HttpStatus.BAD_REQUEST);
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
                return new ResponseEntity<>("Account verified successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid verification code", HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByUsername(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                return new ResponseEntity<>("Account is already verified", HttpStatus.BAD_REQUEST);
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
            return new ResponseEntity<>("Verification code was sent successfully", HttpStatus.OK);
        }

        return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
    }

    public void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();


        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);
        String htmlMessage = templateEngine.process("verification_email.html", context);

        try {
            emailService.sendVerificationEmail(user.getUsername(), subject, htmlMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
