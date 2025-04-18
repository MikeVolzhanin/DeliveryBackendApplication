package ru.volzhanin.deliverybackendapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.volzhanin.deliverybackendapplication.dto.UserDto;
import ru.volzhanin.deliverybackendapplication.entity.User;
import ru.volzhanin.deliverybackendapplication.service.UserService;

@RestController
@RequestMapping("/user")
@Tag(name = "Тестовый контроллер для проверки работы аутентификации",
        description = "Контроллер предоставляет тестовый эндпоинт для получения информации о текущем аутентифицированном пользователе.")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    @Operation(
            summary = "Получение информации о текущем пользователе",
            description = "Возвращает имя текущего аутентифицированного пользователя на основе токена, переданного в запросе."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение имени текущего пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        UserDto userDto = new UserDto(
                currentUser.getFirstName(),
                currentUser.getSurname(),
                currentUser.getMiddleName(),
                currentUser.getPhoneNumber(),
                currentUser.getUsername()
        );
        return ResponseEntity.ok().body(userDto);
    }

    @PostMapping("/me")
    public ResponseEntity<?> updateAuthenticatedUser(@RequestBody UserDto userDto) {
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam String email){
        return userService.deleteUser(email);
    }
}
