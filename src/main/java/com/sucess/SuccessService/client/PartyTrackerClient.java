package com.sucess.SuccessService.client;

import org.springframework.web.client.RestTemplate;
import com.sucess.SuccessService.dto.PartyMember;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PartyTrackerClient {

    private final RestTemplate restTemplate;

    @Value("${partytracker.base-url}")
    private String baseUrl;

    public PartyTrackerClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PartyMember> getLatestParty(String name, String realm) {
        String url = String.format("%s/api/party/latest?name=%s&realm=%s", baseUrl, name, realm);
        ResponseEntity<PartyMember[]> response = restTemplate.getForEntity(url, PartyMember[].class);
        return Arrays.asList(response.getBody());
    }
}