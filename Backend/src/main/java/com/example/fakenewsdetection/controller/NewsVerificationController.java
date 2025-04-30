package com.example.fakenewsdetection.controller;

import com.example.fakenewsdetection.model.NewsArticle;
import com.example.fakenewsdetection.repository.NewsRepository;
import com.example.fakenewsdetection.service.GoogleFactCheckService;
import com.example.fakenewsdetection.service.TrustedNewsVerificationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = "*") 
public class NewsVerificationController {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private GoogleFactCheckService googleFactCheckService;

    @Autowired
    private TrustedNewsVerificationService trustedNewsVerificationService;
    @PostMapping("/verify-news")
    public ResponseEntity<Map<String, String>> verifyNews(@RequestBody Map<String, Object> request) {
        String newsText = (String) request.get("text");
        Boolean isLiveNews = (Boolean) request.get("isLiveNews");
        if (newsText == null || newsText.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("result", "Invalid input"));
        }
        Optional<NewsArticle> existingArticle = newsRepository.findByText(newsText);
        if (existingArticle.isPresent()) {
            return ResponseEntity.ok(Map.of("result", existingArticle.get().getResult()));
        }
        String result;
        if (Boolean.TRUE.equals(isLiveNews)) {
            result = trustedNewsVerificationService.verifyWithTrustedSources(newsText);
        } else {
            result = googleFactCheckService.checkFact(newsText);
        }
        return ResponseEntity.ok(Map.of("result", result));
    }
}
