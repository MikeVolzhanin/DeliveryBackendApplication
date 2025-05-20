package ru.volzhanin.deliverybackendapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.volzhanin.deliverybackendapplication.dto.CompanyDto;
import ru.volzhanin.deliverybackendapplication.service.CompanyService;

@RestController
@RequestMapping("/company")
@Tag(name = "Управление компаниями")
@RequiredArgsConstructor
public class CompanyController {
    public final CompanyService companyService;

    @Operation(
            summary = "Добавить компанию в систему"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Компания успешно добавлена в систему")
    })
    @PostMapping("/add")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> addCompany(@RequestBody CompanyDto companyDto) {
        return companyService.addCompany(companyDto);
    }

    @Operation(
            summary = "Получить список всех компаний"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получен список всех компаний")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/all")
    public ResponseEntity<?> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @Operation(
            summary = "Удалить компанию из системы"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Компания успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Компания не найдена")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        return companyService.deleteCompanyById(id);
    }
}
