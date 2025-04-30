package com.example.fakenewsdetection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.fakenewsdetection.model.NewsArticle;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<NewsArticle, Long> {
    Optional<NewsArticle> findByText(String text);
}
