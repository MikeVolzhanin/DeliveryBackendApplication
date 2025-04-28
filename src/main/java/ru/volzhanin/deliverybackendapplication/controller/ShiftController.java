package ru.volzhanin.deliverybackendapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.volzhanin.deliverybackendapplication.dto.ShiftDto;
import ru.volzhanin.deliverybackendapplication.dto.ShiftFilterDto;
import ru.volzhanin.deliverybackendapplication.entity.User;
import ru.volzhanin.deliverybackendapplication.service.ShiftService;

@RestController
@RequestMapping("/shift")
@Tag(name = "Управление сменами")
@RequiredArgsConstructor
public class ShiftController {
    public final ShiftService shiftService;

    @Operation(
            summary = "Добавление смены в систему"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Смена успешно добавлена"),
        @ApiResponse(responseCode = "404", description = "Компания смены не найдена в системе")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addShift(@RequestBody ShiftDto shiftDto) {
        return shiftService.addShift(shiftDto);
    }

    @Operation(
            summary = "Получение доступных смен"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Все необходимые смены успешно получены")
    })
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableShifts(@RequestBody(required = false) ShiftFilterDto shiftFilterDto) {
        return shiftService.getFilteredShifts(shiftFilterDto);
    }

    @Operation(
            summary = "Получение смены"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Смена успешно получена"),
            @ApiResponse(responseCode = "404", description = "Смена не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getShift(@PathVariable Long id) {
        return shiftService.getShift(id);
    }

    @Operation(
            summary = "Удаление смены"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Смена успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Смена не найдена")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteShift(@PathVariable Long id) {
        return shiftService.deleteShift(id);
    }

    @Operation(
            summary = "Бронирование смены"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Смена успешно забронирована"),
            @ApiResponse(responseCode = "404", description = "Смена не найдена"),
            @ApiResponse(responseCode = "400", description = "Смена уже занята или недоступна"),
            @ApiResponse(responseCode = "409", description = "Смена уже забронирована вами")
    })
    @PostMapping("/select/{id}")
    public ResponseEntity<?> selectShifts(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return shiftService.selectShift(id, currentUser);
    }

    @Operation(
            summary = "Отмена бронирования смены"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бронирование смены успешно отменено"),
            @ApiResponse(responseCode = "404", description = "Бронь не найдена")
    })
    @DeleteMapping("/select/{id}/cancel")
    public ResponseEntity<?> cancelShift(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return shiftService.cancelShift(id, currentUser);
    }

    @Operation(
            summary = "Получение забронированных смен пользователем"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены смены (или пустой массив)")
    })
    @GetMapping("/selected")
    public ResponseEntity<?> getSelectedShifts(@AuthenticationPrincipal User currentUser) {
        return shiftService.getBookedShifts(currentUser);
    }
}
