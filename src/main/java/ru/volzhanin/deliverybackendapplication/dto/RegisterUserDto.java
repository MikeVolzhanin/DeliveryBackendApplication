package ru.volzhanin.deliverybackendapplication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    private String firstName;
    private String password;
    private String email;
    private String surname;
    private String middleName;
    private String phoneNumber;
}
