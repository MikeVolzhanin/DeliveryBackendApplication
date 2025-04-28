package ru.volzhanin.deliverybackendapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Управление пользователями")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    @Operation(
            summary = "Получение информации о текущем пользователе",
            description = "Возвращает информацию о текущем пользователе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение информации о пользователе"),
    })
    @SecurityRequirement(name = "bearerAuth")
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

    @Operation(
            summary =  "Обновление данных текущего пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлёны"),
            @ApiResponse(responseCode = "400", description = "Пользователь не найден")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/me")
    public ResponseEntity<?> updateAuthenticatedUser(@RequestBody UserDto userDto) {
        return userService.updateUser(userDto);
    }

    @Operation(
            summary = "Удаление пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удалён"),
            @ApiResponse(responseCode = "400", description = "Пользователь не найден")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam String email){
        return userService.deleteUser(email);
    }
}
