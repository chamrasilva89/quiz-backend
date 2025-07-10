package com.sasip.quizz.repository;

import com.sasip.quizz.model.SasipNews;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SasipNewsRepository extends JpaRepository<SasipNews, Long> {
    // Custom queries for pagination or other specific use cases
    
    // Find all news (this is equivalent to getAllNews with pagination)
    Page<SasipNews> findAll(Pageable pageable);

    // You can also add custom queries based on specific fields if needed
    // For example, to find news by type
    Page<SasipNews> findByType(String type, Pageable pageable);
    
    // Find a specific news item by its title
    SasipNews findByTitle(String title);
    
    // Add other custom methods for filtering or searching if needed
}
