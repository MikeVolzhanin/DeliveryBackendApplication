package ru.volzhanin.deliverybackendapplication.dto;

import lombok.Data;

@Data
public class CompanyDto {
    private Long id;
    private String name;
    private String contactInfo;
}
