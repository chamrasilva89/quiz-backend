package com.sasip.quizz.repository;

import com.sasip.quizz.model.AlYearModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlYearModuleRepository extends JpaRepository<AlYearModule, Long> {

    // Find modules completed for a specific AL Year
    List<AlYearModule> findByAlYearId(Long alYearId);

    // Find a completed module by AL Year and Module
    AlYearModule findByAlYearIdAndModuleId(Long alYearId, Long moduleId);

    // Find completed modules by AL Year and Module (optional submodule)
    List<AlYearModule> findByAlYearIdAndModuleIdAndSubmoduleId(Long alYearId, Long moduleId, Long submoduleId);
}
