package com.sasip.quizz.spec;

import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.QuizStatus;
import com.sasip.quizz.model.QuizType;
import jakarta.persistence.criteria.Predicate;


import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class QuizSpecifications {

    public static Specification<Quiz> isSasip() {
        return (root, query, cb) -> cb.equal(root.get("quizType"), QuizType.SASIP);
    }

    public static Specification<Quiz> hasStatus(QuizStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("quizStatus"), status);
        };
    }

    public static Specification<Quiz> hasAlYear(String alYear) {
        return (root, query, cb) -> {
            if (alYear == null || alYear.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("alYear"), alYear);
        };
    }

    public static Specification<Quiz> hasAnyModule(List<String> modules) {
        return (root, query, cb) -> {
            // If modules list is null or empty, don't apply any filter
            if (modules == null || modules.isEmpty()) {
                return cb.conjunction(); // This means "no restriction"
            }

            // Create a list of 'LIKE' predicates, one for each module
            List<Predicate> predicates = new ArrayList<>();
            for (String module : modules) {
                // We search for `"%\"moduleName\"%"` to match the module within the JSON array string
                predicates.add(cb.like(root.get("modules"), "%\"" + module + "\"%"));
            }

            // Combine the predicates with an 'OR' condition
            // This finds quizzes that have AT LEAST ONE of the specified modules
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }


    public static Specification<Quiz> timeLimitBetween(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get("timeLimit"), min, max);
            } else if (min != null) {
                return cb.ge(root.get("timeLimit"), min);
            } else if (max != null) {
                return cb.le(root.get("timeLimit"), max);
            } else {
                return cb.conjunction();
            }
        };
    }

    
}
