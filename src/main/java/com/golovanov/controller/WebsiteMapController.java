package com.golovanov.controller;

import com.golovanov.service.WebsiteMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class WebsiteMapController {

    private final WebsiteMapService websiteMapService;

    @SuppressWarnings("rawtypes")
    @GetMapping("/")
    public ResponseEntity getWebsiteMap(String path) {
        ResponseEntity response = websiteMapService.getWebsiteMap(path);
        return new ResponseEntity<>(response.getStatusCode());
    }
}
