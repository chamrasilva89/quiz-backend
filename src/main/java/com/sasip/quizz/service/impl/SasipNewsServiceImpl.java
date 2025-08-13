package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.SasipNewsRequest;
import com.sasip.quizz.dto.NewsListResponse;
import com.sasip.quizz.model.SasipNews;
import com.sasip.quizz.repository.SasipNewsRepository;
import com.sasip.quizz.service.SasipNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SasipNewsServiceImpl implements SasipNewsService {

    @Autowired
    private SasipNewsRepository sasipNewsRepository;

    @Override
    public SasipNews createNews(SasipNewsRequest request) {
        SasipNews news = new SasipNews();
        news.setTitle(request.getTitle());
        news.setShortDescription(request.getShortDescription());
        news.setLongDescription(request.getLongDescription());
        news.setType(request.getType());
        news.setImageUrl(request.getImageUrl());
        news.setImageBase64(request.getImageBase64());  // Handle imageBase64
        news.setPublishDateTime(request.getPublishDateTime());
        return sasipNewsRepository.save(news);
    }

    @Override
    public SasipNews updateNews(Long newsId, SasipNewsRequest request) {
        SasipNews existingNews = sasipNewsRepository.findById(newsId).orElseThrow();
        existingNews.setTitle(request.getTitle());
        existingNews.setShortDescription(request.getShortDescription());
        existingNews.setLongDescription(request.getLongDescription());
        existingNews.setType(request.getType());
        existingNews.setImageUrl(request.getImageUrl());
        existingNews.setImageBase64(request.getImageBase64());  // Handle imageBase64
        existingNews.setPublishDateTime(request.getPublishDateTime());
        return sasipNewsRepository.save(existingNews);
    }

    @Override
    public void deleteNews(Long newsId) {
        SasipNews existingNews = sasipNewsRepository.findById(newsId).orElseThrow();
        sasipNewsRepository.delete(existingNews);
    }

@Override
public Page<NewsListResponse> getAllNews(Pageable pageable) {
    // Apply sorting by publishDateTime in descending order
    Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("publishDateTime")));

    // Use repository method to fetch paginated results with sorting
    Page<SasipNews> newsPage = sasipNewsRepository.findAll(sortedPageable);

    // Convert SasipNews to NewsListResponse
    return newsPage.map(news -> {
        NewsListResponse response = new NewsListResponse();
        response.setNewsId(news.getNewsId());
        response.setTitle(news.getTitle());
        response.setShortDescription(news.getShortDescription());
        response.setImageUrl(news.getImageUrl());
        response.setImageBase64(news.getImageBase64());  // Add imageBase64
        response.setPublishDateTime(news.getPublishDateTime());
        return response;
    });
}


    @Override
    public SasipNews getNewsById(Long newsId) {
        return sasipNewsRepository.findById(newsId).orElseThrow();
    }
}
