package ru.volzhanin.deliverybackendapplication.dto;

import lombok.Data;
import ru.volzhanin.deliverybackendapplication.entity.DeliveryType;
import ru.volzhanin.deliverybackendapplication.entity.ShiftStatus;

import java.time.LocalDateTime;

@Data
public class ShiftDto {
    private Long id;
    private Long companyId; // Только ID компании, не вся сущность
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Integer locationLatitude;
    private Integer locationLongitude;
    private Integer minRate;
    private Integer avgRate;
    private ShiftStatus status;
    private DeliveryType requiredDeliveryType;
}
