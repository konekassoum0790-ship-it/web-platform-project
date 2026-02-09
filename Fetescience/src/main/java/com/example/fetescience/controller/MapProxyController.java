package com.example.fetescience.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class MapProxyController {

    @GetMapping("/api/geocode")
    public Object searchAddress(@RequestParam String address) {

        String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + address;


        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "FeteScienceApp/1.0 (contact@fetescience.local)");

        HttpEntity<String> entity = new HttpEntity<>(headers);


        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Object.class
        );

        return response.getBody();
    }
}
