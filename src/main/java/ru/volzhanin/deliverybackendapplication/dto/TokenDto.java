package ru.volzhanin.deliverybackendapplication.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TokenDto {
    private String accessToken;
    private String refreshToken;
}
