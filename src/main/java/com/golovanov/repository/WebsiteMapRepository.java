package com.golovanov.repository;

import com.golovanov.entity.WebPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebsiteMapRepository extends JpaRepository<WebPage, Integer> {
    Optional<WebPage> findByPath(String path);

    @Override
    void deleteAll();
}
