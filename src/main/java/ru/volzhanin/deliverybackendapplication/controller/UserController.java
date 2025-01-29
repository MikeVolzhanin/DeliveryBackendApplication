package ru.volzhanin.deliverybackendapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.volzhanin.deliverybackendapplication.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "Тестовый контроллер для проверки работы аутентификации",
        description = "Контроллер предоставляет тестовый эндпоинт для получения информации о текущем аутентифицированном пользователе.")
public class UserController {
    @Operation(
            summary = "Получение информации о текущем пользователе",
            description = "Возвращает имя текущего аутентифицированного пользователя на основе токена, переданного в запросе."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение имени текущего пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @GetMapping("/me")
    public ResponseEntity<String> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser.getFirstName());
    }
}
