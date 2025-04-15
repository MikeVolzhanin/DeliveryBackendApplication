package ru.volzhanin.deliverybackendapplication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.volzhanin.deliverybackendapplication.dto.ShiftDto;
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
}
