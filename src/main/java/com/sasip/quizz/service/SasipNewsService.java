package com.sasip.quizz.service;

import com.sasip.quizz.dto.SasipNewsRequest;
import com.sasip.quizz.dto.NewsListResponse;
import com.sasip.quizz.model.SasipNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SasipNewsService {
    SasipNews createNews(SasipNewsRequest request);
    SasipNews updateNews(Long newsId, SasipNewsRequest request);
    void deleteNews(Long newsId);
    Page<NewsListResponse> getAllNews(Pageable pageable);  // Updated to use pagination
    SasipNews getNewsById(Long newsId);
}
