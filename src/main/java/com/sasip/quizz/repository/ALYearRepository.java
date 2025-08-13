package com.sasip.quizz.repository;

import com.sasip.quizz.model.ALYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ALYearRepository extends JpaRepository<ALYear, Long> {

    Optional<ALYear> findByYear(Integer year);

    Optional<ALYear> findByIsCurrent(Boolean isCurrent);

    boolean existsByYear(Integer year);  // Check if a year already exists

    @Modifying
    @Query("UPDATE ALYear a SET a.isCurrent = false WHERE a.isCurrent = true")
    void deactivateCurrentYear();  // Deactivate the current year
}
