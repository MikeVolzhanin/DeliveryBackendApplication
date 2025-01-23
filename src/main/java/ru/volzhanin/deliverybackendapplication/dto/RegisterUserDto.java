package ru.volzhanin.deliverybackendapplication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    private String email;
    private String password;
    private String username;
    private String surname;
    private String middleName;
    private String phoneNumber;
}
