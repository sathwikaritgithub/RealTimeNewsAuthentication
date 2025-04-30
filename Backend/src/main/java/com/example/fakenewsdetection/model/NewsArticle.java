package com.example.fakenewsdetection.model;

import jakarta.persistence.*;

@Entity
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, length = 1000)
    private String text;

    private String result;

    public NewsArticle() {}

    public NewsArticle(String text, String result) {
        this.text = text;
        this.result = result;
    }

    public String getText() { return text; }
    public String getResult() { return result; }
}
