package ru.volzhanin.deliverybackendapplication.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private String firstName;
    private String surname;
    private String middleName;
    private String phone;
    private String email;
}
