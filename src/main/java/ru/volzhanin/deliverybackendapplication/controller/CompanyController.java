package ru.volzhanin.deliverybackendapplication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.volzhanin.deliverybackendapplication.entity.Company;
import ru.volzhanin.deliverybackendapplication.service.CompanyService;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {
    public final CompanyService companyService;

    @PostMapping("/add")
    public ResponseEntity<?> addCompany(@RequestBody Company company) {
        return companyService.addCompany(company);
    }
}
