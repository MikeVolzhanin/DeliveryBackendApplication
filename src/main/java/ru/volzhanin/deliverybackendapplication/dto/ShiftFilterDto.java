package ru.volzhanin.deliverybackendapplication.dto;

import lombok.Data;
import ru.volzhanin.deliverybackendapplication.entity.DeliveryType;
import ru.volzhanin.deliverybackendapplication.entity.ShiftStatus;

import java.time.LocalDateTime;

@Data
public class ShiftFilterDto {
    private Long companyId;
    private LocalDateTime startAfter;
    private LocalDateTime endBefore;
    private Integer minRateFrom;
    private ShiftStatus status;
    private DeliveryType requiredDeliveryType;
}
