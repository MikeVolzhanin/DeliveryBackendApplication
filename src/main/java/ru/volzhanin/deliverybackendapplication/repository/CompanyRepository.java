package ru.volzhanin.deliverybackendapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.volzhanin.deliverybackendapplication.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
