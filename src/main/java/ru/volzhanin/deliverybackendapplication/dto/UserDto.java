package ru.volzhanin.deliverybackendapplication.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class UserDto {
    private String firstName;
    private String phone;
    private String email;

    public UserDto(String firstName, String phone, String email) {
        this.firstName = firstName;
        this.phone = phone;
        this.email = email;
    }
}
