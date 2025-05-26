package com.sasip.quizz.repository;

import com.sasip.quizz.model.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {
    Optional<District> findByCode(String code);
    boolean existsByCode(String code);
}
