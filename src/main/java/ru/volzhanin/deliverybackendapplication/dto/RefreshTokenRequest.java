package ru.volzhanin.deliverybackendapplication.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
