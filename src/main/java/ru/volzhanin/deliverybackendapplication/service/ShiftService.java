package ru.volzhanin.deliverybackendapplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import ru.volzhanin.deliverybackendapplication.dto.ShiftDto;
import ru.volzhanin.deliverybackendapplication.entity.Company;
import ru.volzhanin.deliverybackendapplication.entity.Shift;
import ru.volzhanin.deliverybackendapplication.repository.CompanyRepository;
import ru.volzhanin.deliverybackendapplication.repository.ShiftRepository;

@Service
@RequiredArgsConstructor
public class ShiftService {
    public final ShiftRepository shiftRepository;
    public final CompanyRepository companyRepository;

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
        shift.setRequiredDeliveryType(shiftDto.getRequiredDeliveryType());

        Shift savedShift = shiftRepository.save(shift);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShift);
    }

}
