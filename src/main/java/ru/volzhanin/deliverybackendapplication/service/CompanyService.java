package ru.volzhanin.deliverybackendapplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.volzhanin.deliverybackendapplication.dto.CompanyDto;
import ru.volzhanin.deliverybackendapplication.entity.Company;
import ru.volzhanin.deliverybackendapplication.entity.User;
import ru.volzhanin.deliverybackendapplication.repository.CompanyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
    public final CompanyRepository companyRepository;

    public ResponseEntity<?> addCompany(CompanyDto companyDto) {
        Company company = new Company();
        company.setName(companyDto.getName());
        company.setContactInfo(companyDto.getContactInfo());

        Company savedCompany = companyRepository.save(company);
        return new ResponseEntity<>(savedCompany, HttpStatus.OK);
    }

    public ResponseEntity<List<CompanyDto>> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();

        List<CompanyDto> companyDtos = companies.stream().map(company -> {
            CompanyDto dto = new CompanyDto();
            dto.setId(company.getId());
            dto.setName(company.getName());
            dto.setContactInfo(company.getContactInfo());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(companyDtos);
    }

    public ResponseEntity<?> deleteCompanyById(Long id) {
        Optional<Company> optionalCompany = companyRepository.findById(id);

        if (optionalCompany.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Компания с ID " + id + " не найдена.");
        }

        Company company = optionalCompany.get();

        // Открепляем компанию от пользователей (если важно)
        for (User user : company.getUsers()) {
            user.getCompanies().remove(company);
        }

        companyRepository.delete(company);

        return ResponseEntity.ok("Компания с ID " + id + " успешно удалена.");
    }
}
