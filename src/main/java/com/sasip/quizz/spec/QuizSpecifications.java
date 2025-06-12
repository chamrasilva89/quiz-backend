package com.sasip.quizz.spec;

import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.QuizStatus;
import com.sasip.quizz.model.QuizType;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public class QuizSpecifications {

    public static Specification<Quiz> isSasip() {
        return (root, query, cb) -> cb.equal(root.get("quizType"), QuizType.SASIP);
    }

    public static Specification<Quiz> hasStatus(QuizStatus status) {
        return (root, query, cb) -> cb.equal(root.get("quizStatus"), status);
    }

public static Specification<Quiz> hasAnyModule(List<String> modules) {
    return (root, query, cb) -> {
        if (modules == null || modules.isEmpty()) {
            return cb.conjunction();
        }

        // Convert list to JSON array string e.g., ["Math","Science"]
        String jsonArray = "[" + modules.stream()
                .map(m -> "\"" + m + "\"")
                .reduce((m1, m2) -> m1 + "," + m2)
                .orElse("") + "]";

        // JSON_OVERLAPS(modules, '["Math", "Science"]')
        return cb.isTrue(cb.function(
            "JSON_OVERLAPS",
            Boolean.class,
            root.get("modules"), // not moduleList
            cb.literal(jsonArray)
        ));
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
