package ru.volzhanin.deliverybackendapplication.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterUserDto {
    private String firstName;
    private String password;
    private String email;
    private String surname;
    private String middleName;
    private String phoneNumber;
}
