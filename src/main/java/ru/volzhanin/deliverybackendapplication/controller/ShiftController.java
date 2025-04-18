package ru.volzhanin.deliverybackendapplication.controller;

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
@RequiredArgsConstructor
public class ShiftController {
    public final ShiftService shiftService;

    @PostMapping("/add")
    public ResponseEntity<?> addShift(@RequestBody ShiftDto shiftDto) {
        return shiftService.addShift(shiftDto);
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableShifts(@RequestBody(required = false) ShiftFilterDto shiftFilterDto) {
        return shiftService.getFilteredShifts(shiftFilterDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShift(@PathVariable Long id) {
        return shiftService.getShift(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteShift(@PathVariable Long id) {
        return shiftService.deleteShift(id);
    }

    @PostMapping("/select/{id}")
    public ResponseEntity<?> selectShifts(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return shiftService.selectShift(id, currentUser);
    }

    @DeleteMapping("/select/{id}/cancel")
    public ResponseEntity<?> cancelShift(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return shiftService.cancelShift(id, currentUser);
    }

    @GetMapping("/selected")
    public ResponseEntity<?> getSelectedShifts(@AuthenticationPrincipal User currentUser) {
        return shiftService.getBookedShifts(currentUser);
    }
}
