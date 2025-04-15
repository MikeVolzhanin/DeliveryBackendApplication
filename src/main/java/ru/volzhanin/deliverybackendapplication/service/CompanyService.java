package ru.volzhanin.deliverybackendapplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.volzhanin.deliverybackendapplication.entity.Company;
import ru.volzhanin.deliverybackendapplication.repository.CompanyRepository;

@Service
@RequiredArgsConstructor
public class CompanyService {
    public final CompanyRepository companyRepository;

    public ResponseEntity<?> addCompany(Company company) {
        Company savedCompany = companyRepository.save(company);
        return new ResponseEntity<>(savedCompany, HttpStatus.OK);
    }

}
