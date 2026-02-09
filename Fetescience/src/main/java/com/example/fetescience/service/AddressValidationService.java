package com.example.fetescience.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AddressValidationService {

    public boolean isValidAddress(String address) {
        try {
            String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + encoded;

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            return response != null && response.contains("\"lat\"");
        } catch (Exception e) {
            return false;
        }
    }
}
