package com.golovanov.service;

import com.golovanov.entity.WebPage;
import com.golovanov.mapper.WebsiteMapper;
import com.golovanov.repository.WebsiteMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

@Service
public class WebsiteMapService {

    private LinkedHashSet<WebPage> websiteMap;

    private volatile ConcurrentHashMap<String, String> checkedLinks = new ConcurrentHashMap<>();

    @Autowired
    private WebsiteMapRepository repository;

    @SuppressWarnings("rawtypes")
    public ResponseEntity getWebsiteMap(String path) {
        path = slashCheck(path);
        websiteMap = new ForkJoinPool().invoke(new WebsiteMapper(checkedLinks, path, path));

        checkedLinks.clear();

        if (websiteMap.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // delete previous data
        // maybe it is better to replace old data and delete them after successful saving of new one...
        repository.deleteAll();
        repository.saveAll(websiteMap);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String slashCheck(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }
}
