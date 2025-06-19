package com.sucess.SuccessService.client;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import com.sucess.SuccessService.dto.HydratedPlayer;

@Service
public class SuccessScoreCalculationHydrationClient {

    private final RestTemplate restTemplate;

    @Value("${scorecalc.base-url}")
    private String baseUrl;

    public SuccessScoreCalculationHydrationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getPlayerScore(HydratedPlayer player, int targetLevel) {
        URI uri = URI.create(String.format("%s/api/calculate/score?targetLevel=%d", baseUrl, targetLevel));
        return restTemplate.postForObject(uri, player, Double.class);
    }
}