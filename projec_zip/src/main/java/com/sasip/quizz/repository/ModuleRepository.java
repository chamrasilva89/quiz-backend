package com.sasip.quizz.repository;

import com.sasip.quizz.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<Module, Long> {}
