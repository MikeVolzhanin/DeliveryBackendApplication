package ru.volzhanin.deliverybackendapplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import ru.volzhanin.deliverybackendapplication.dto.ShiftDto;
import ru.volzhanin.deliverybackendapplication.dto.ShiftFilterDto;
import ru.volzhanin.deliverybackendapplication.entity.*;
import ru.volzhanin.deliverybackendapplication.repository.CompanyRepository;
import ru.volzhanin.deliverybackendapplication.repository.ShiftRepository;
import ru.volzhanin.deliverybackendapplication.repository.UserRepository;
import ru.volzhanin.deliverybackendapplication.repository.UserShiftRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftService {
    public final ShiftRepository shiftRepository;
    public final CompanyRepository companyRepository;
    private final UserShiftRepository userShiftRepository;

    public ResponseEntity<?> addShift(@RequestBody ShiftDto shiftDto) {
        Company company = companyRepository.findById(shiftDto.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        Shift shift = new Shift();
        shift.setCompany(company);
        shift.setLocation(shiftDto.getLocation());
        shift.setLocationLatitude(shiftDto.getLocationLatitude());
        shift.setLocationLongitude(shiftDto.getLocationLongitude());
        shift.setAvgRate(shiftDto.getAvgRate());
        shift.setStartTime(shiftDto.getStartTime());
        shift.setEndTime(shiftDto.getEndTime());
        shift.setMinRate(shiftDto.getMinRate());
        shift.setStatus(shiftDto.getStatus());
        shift.setRequiredDeliveryType(shiftDto.getRequiredDeliveryType());

        shiftRepository.save(shift);
        return new ResponseEntity<>(shiftDto, HttpStatus.CREATED);
    }

    public ResponseEntity<List<ShiftDto>> getFilteredShifts(ShiftFilterDto filter) {
        List<Shift> shifts = shiftRepository.findAll();

        // Если фильтр не передан — вернём все смены
        if (filter == null) {
            return ResponseEntity.ok(
                    shifts.stream().filter(shift -> shift.getStatus() == ShiftStatus.OPEN)
                            .map(this::mapToDto)
                            .collect(Collectors.toList())
            );
        }

        List<ShiftDto> result = shifts.stream()
                .filter(shift -> filter.getCompanyId() == null || shift.getCompany().getId().equals(filter.getCompanyId()))
                .filter(shift -> filter.getStartAfter() == null || shift.getStartTime().isAfter(filter.getStartAfter()))
                .filter(shift -> filter.getEndBefore() == null || shift.getEndTime().isBefore(filter.getEndBefore()))
                .filter(shift -> filter.getMinRateFrom() == null || shift.getMinRate() >= filter.getMinRateFrom())
                .filter(shift -> filter.getStatus() == null || shift.getStatus() == filter.getStatus())
                .filter(shift -> filter.getRequiredDeliveryType() == null || shift.getRequiredDeliveryType() == filter.getRequiredDeliveryType())
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> deleteShift(@PathVariable Long id) {
        Optional<Shift> optionalShift = shiftRepository.findById(id);

        if (optionalShift.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Смена с ID " + id + " не найдена.");
        }

        shiftRepository.delete(optionalShift.get());
        return ResponseEntity.ok("Смена успешно удалена.");
    }

    public ResponseEntity<?> getShift(@PathVariable Long id) {
        Optional<Shift> optionalShift = shiftRepository.findById(id);
        if (optionalShift.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Смена с ID " + id + " не найдена.");
        }
        return ResponseEntity.ok(mapToDto(optionalShift.get()));
    }

    public ResponseEntity<?> selectShift(Long id, User currentUser) {
        Optional<Shift> optionalShift = shiftRepository.findById(id);

        if (optionalShift.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Смена не найдена");
        }

        Shift shift = optionalShift.get();

        if (!shift.getStatus().equals(ShiftStatus.OPEN)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Смена уже занята или недоступна");
        }

        boolean alreadyBooked = userShiftRepository.existsByUserIdAndShiftId(currentUser.getId(), shift.getId());
        if (alreadyBooked) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Вы уже забронировали эту смену");
        }

        // Создание брони
        UserShift userShift = new UserShift();
        userShift.setUser(currentUser);
        userShift.setShift(shift);
        userShift.setBookedAt(LocalDateTime.now());
        userShiftRepository.save(userShift);

        // Обновление статуса смены
        shift.setStatus(ShiftStatus.BOOKED);
        shiftRepository.save(shift);

        return ResponseEntity.ok("Смена успешно забронирована");
    }

    public ResponseEntity<?> cancelShift(Long id, User currentUser) {
        Optional<UserShift> optionalUserShift = userShiftRepository.findByUserIdAndShiftId(currentUser.getId(), id);

        if (optionalUserShift.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Бронь не найдена");
        }

        UserShift userShift = optionalUserShift.get();

        // Отмечаем дату отмены
        userShift.setCanceledAt(LocalDateTime.now());
        userShiftRepository.save(userShift);

        // Удаляем связь
        userShiftRepository.delete(userShift);

        // Обновляем статус смены обратно на OPEN
        Shift shift = userShift.getShift();
        shift.setStatus(ShiftStatus.OPEN);
        shiftRepository.save(shift);

        return ResponseEntity.ok("Бронирование смены отменено");
    }

    public ResponseEntity<?> completeShift(Long id, User currentUser) {
        Optional<UserShift> optionalUserShift = userShiftRepository.findByUserIdAndShiftId(currentUser.getId(), id);

        if (optionalUserShift.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Бронь не найдена");
        }

        UserShift userShift = optionalUserShift.get();
        Shift shift = userShift.getShift();

        // Проверка: можно ли завершить смену
        if (!ShiftStatus.BOOKED.equals(shift.getStatus())) {
            return ResponseEntity.badRequest().body("Смену можно завершить только если она в статусе BOOKED");
        }

        // Дополнительно можно проверить, что текущее время >= времени окончания смены
        if (shift.getEndTime().isAfter(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Нельзя завершить смену до её окончания");
        }

        // Обновляем статус на COMPLETED
        shift.setStatus(ShiftStatus.COMPLETED);
        shiftRepository.save(shift);

        return ResponseEntity.ok("Смена завершена");
    }


    public ResponseEntity<List<ShiftDto>> getBookedShifts(User currentUser) {
        List<UserShift> userShifts = userShiftRepository.findByUserIdAndCanceledAtIsNull(currentUser.getId());

        List<ShiftDto> result = userShifts.stream()
                .map(UserShift::getShift)
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    private ShiftDto mapToDto(Shift shift) {
        ShiftDto dto = new ShiftDto();
        dto.setId(shift.getId());
        dto.setCompanyId(shift.getCompany().getId());
        dto.setStartTime(shift.getStartTime());
        dto.setEndTime(shift.getEndTime());
        dto.setLocation(shift.getLocation());
        dto.setLocationLatitude(shift.getLocationLatitude());
        dto.setLocationLongitude(shift.getLocationLongitude());
        dto.setMinRate(shift.getMinRate());
        dto.setAvgRate(shift.getAvgRate());
        dto.setStatus(shift.getStatus());
        dto.setRequiredDeliveryType(shift.getRequiredDeliveryType());
        return dto;
    }
}
