package com.sucess.SuccessService.client;

import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sucess.SuccessService.dto.HydratedPlayer;

@Service
public class RaiderIOHydrationClient {

    private final RestTemplate restTemplate;

    @Value("${raiderio.base-url}")
    private String baseUrl;

    public RaiderIOHydrationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public HydratedPlayer hydrate(String region, String realm, String name) {
        String url = String.format("%s/api/hydrate/character?region=%s&realm=%s&name=%s", baseUrl, region, realm, name);
        return restTemplate.getForObject(url, HydratedPlayer.class);
    }
}
