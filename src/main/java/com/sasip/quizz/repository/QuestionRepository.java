package com.sasip.quizz.repository;

import com.sasip.quizz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuestionIdNotIn(List<Long> ids); 
    List<Question> findByDifficultyLevel(String difficultyLevel);
    List<Question> findByDifficultyLevelAndQuestionIdNotIn(String difficultyLevel, List<Long> ids);

    @Query("""
        SELECT q FROM Question q 
        WHERE 
            (:#{#modules == null || #modules.isEmpty()} = true OR q.module IN :modules) 
            AND (:#{#submodules == null || #submodules.isEmpty()} = true OR q.submodule IN :submodules) 
            AND (:#{#difficultyLevels == null || #difficultyLevels.isEmpty()} = true OR q.difficultyLevel IN :difficultyLevels)
        """)
    Page<Question> findFilteredQuestions(
        @Param("modules") List<String> modules,
        @Param("submodules") List<String> submodules,
        @Param("difficultyLevels") List<String> difficultyLevels,
        Pageable pageable
    );

}
