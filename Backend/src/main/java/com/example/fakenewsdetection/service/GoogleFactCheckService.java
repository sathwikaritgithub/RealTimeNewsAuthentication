package com.example.fakenewsdetection.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.example.fakenewsdetection.model.NewsArticle;
import com.example.fakenewsdetection.repository.NewsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GoogleFactCheckService {

	@Autowired
    private NewsRepository newsRepository;
	
    @Value("${google.factcheck.api.key}")
    private String API_KEY;  

    private static final String GOOGLE_API_URL = "https://factchecktools.googleapis.com/v1alpha1/claims:search?key=";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); 

    public String checkFact(String query) {
        try {
            System.out.println("Inside checkFact method. Query: " + query);

           
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = GOOGLE_API_URL + API_KEY + "&query=" + encodedQuery;

            
            System.out.println("Calling Google Fact Check API with URL: " + url);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseFactCheckResponse(response.getBody(),query);
            } else {
                System.err.println("Error: Received non-success status from Google API: " + response.getStatusCode());
            }
        } catch (Exception e) {
            
            System.err.println("Exception occurred while calling Google Fact Check API: " + e.getMessage());
            e.printStackTrace();
        }

        return "Unable to verify with Google API. Proceeding with AI-based verification.";
    }



    private String parseFactCheckResponse(String jsonResponse,String query) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (root.has("claims") && root.get("claims").isArray() && root.get("claims").size() > 0) {
                JsonNode firstClaim = root.get("claims").get(0);

                if (firstClaim.has("claimReview") && firstClaim.get("claimReview").isArray() && firstClaim.get("claimReview").size() > 0) {
                    JsonNode claimReview = firstClaim.get("claimReview").get(0);
                    String publisher = claimReview.has("publisher") ? claimReview.get("publisher").get("name").asText() : "Unknown Source";
                    String verdict = claimReview.has("textualRating") ? claimReview.get("textualRating").asText() : "Not Available";
                    String reviewUrl = claimReview.has("url") ? claimReview.get("url").asText() : "No URL Available";
                    
                    String result="Fact Checked by " + publisher + ": " + verdict + ". Read more: " + reviewUrl;
                    NewsArticle newArticle = new NewsArticle(query, result);
                    newsRepository.save(newArticle);
                    return result;
                } else {
                    return "Fact check information is available but incomplete.";
                }
            } else {
                return "No proper resources are available to fact check this news.";
            }
        } catch (Exception e) {
            System.err.println("Error parsing Google Fact Check API response: " + e.getMessage());
        }

        return "Error processing fact-check response.";
    }
}
