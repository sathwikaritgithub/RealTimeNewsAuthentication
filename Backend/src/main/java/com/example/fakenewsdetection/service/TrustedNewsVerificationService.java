package com.example.fakenewsdetection.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.example.fakenewsdetection.model.NewsArticle;
import com.example.fakenewsdetection.repository.NewsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TrustedNewsVerificationService {
	
	@Autowired
    private NewsRepository newsRepository;

    private static final String TRUSTED_NEWS_API_URL = "https://newsapi.org/v2/everything?q=";
    private static final String API_KEY = "9e3acfb1d9934745a7ae4d64c6e9b2ba";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String verifyWithTrustedSources(String query) {
        String url = TRUSTED_NEWS_API_URL + query + "&apiKey=" + API_KEY;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseTrustedNewsResponse(response.getBody(),query);
            }
        } catch (Exception e) {
            System.err.println("Error calling Trusted News API: " + e.getMessage());
        }

        return "No trusted source verification found.";
    }

    private String parseTrustedNewsResponse(String jsonResponse,String query) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (root.has("articles") && root.get("articles").size() > 0) {
                JsonNode firstArticle = root.get("articles").get(0);
                String source = firstArticle.has("source") ? firstArticle.get("source").get("name").asText() : "Unknown Source";
                String title = firstArticle.has("title") ? firstArticle.get("title").asText() : "No Title Available";
                
                
                String result="Verified by " + source + ": " + title;
                NewsArticle newArticle = new NewsArticle(query, result);
                newsRepository.save(newArticle);
                return result;
            }
        } catch (Exception e) {
            System.err.println("Error parsing Trusted News API response: " + e.getMessage());
        }

        return "No matching trusted news found.";
    }
}
