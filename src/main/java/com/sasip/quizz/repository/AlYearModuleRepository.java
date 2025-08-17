package com.sasip.quizz.repository;

import com.sasip.quizz.model.AlYearModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlYearModuleRepository extends JpaRepository<AlYearModule, Long> {

    // Find modules completed for a specific AL Year
    List<AlYearModule> findByAlYearId(Long alYearId);

    // Find a completed module by AL Year and Module
    AlYearModule findByAlYearIdAndModuleId(Long alYearId, Long moduleId);

    // Find completed modules by AL Year and Module (optional submodule)
    List<AlYearModule> findByAlYearIdAndModuleIdAndSubmoduleId(Long alYearId, Long moduleId, Long submoduleId);

    // Custom query to find an existing AL Year Module by AL Year and Module ID
    @Query("SELECT a FROM AlYearModule a WHERE a.alYearId = :alYearId AND a.moduleId = :moduleId")
    Optional<AlYearModule> findByAlYearIdAndModuleIdOptional(Long alYearId, Long moduleId);

    // Custom query to check if a record exists for update logic
    @Query("SELECT COUNT(a) > 0 FROM AlYearModule a WHERE a.alYearId = :alYearId AND a.moduleId = :moduleId")
    boolean existsByAlYearIdAndModuleId(Long alYearId, Long moduleId);
}
