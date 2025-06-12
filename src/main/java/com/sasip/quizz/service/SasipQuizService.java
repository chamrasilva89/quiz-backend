// com.sasip.quizz.service.SasipQuizService.java
package com.sasip.quizz.service;

import com.sasip.quizz.dto.SasipQuizFilterRequest;
import com.sasip.quizz.dto.SasipQuizSummary;
import org.springframework.data.domain.Page;

public interface SasipQuizService {
    Page<SasipQuizSummary> findFiltered(SasipQuizFilterRequest filter);
}
