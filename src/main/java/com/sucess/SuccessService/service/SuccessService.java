package com.sucess.SuccessService.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sucess.SuccessService.client.PartyTrackerClient;
import com.sucess.SuccessService.client.RaiderIOHydrationClient;
import com.sucess.SuccessService.client.SuccessScoreCalculationHydrationClient;
import com.sucess.SuccessService.dto.HydratedPlayer;
import com.sucess.SuccessService.dto.HydratedSuccessPlayer;
import com.sucess.SuccessService.dto.PartyMember;
import com.sucess.SuccessService.dto.PartySuccessResponse;
import org.springframework.stereotype.Service;

@Service
public class SuccessService {
    private final PartyTrackerClient partyClient;
    private final RaiderIOHydrationClient hydrationClient;
    private final SuccessScoreCalculationHydrationClient scoreCalculatorClient;

    private static final Logger log = LoggerFactory.getLogger(SuccessService.class);

    public SuccessService(
            PartyTrackerClient partyClient,
            RaiderIOHydrationClient hydrationClient,
            SuccessScoreCalculationHydrationClient scoreCalculatorClient) {
        this.partyClient = partyClient;
        this.hydrationClient = hydrationClient;
        this.scoreCalculatorClient = scoreCalculatorClient;
    }

    public List<HydratedSuccessPlayer> getHydratedParty(int targetLevel, String name, String realm) {
        log.info("Fetching latest party for name={}, realm={}, targetLevel={}", name, realm, targetLevel);

        List<PartyMember> baseParty;
        try {
            baseParty = partyClient.getLatestParty(name, realm);
            log.info("Successfully fetched party of size: {}", baseParty.size());
        } catch (Exception e) {
            log.error("Failed to fetch latest party: {}", e.getMessage(), e);
            throw e;
        }

        List<HydratedSuccessPlayer> hydratedParty = baseParty.stream().map(member -> {
            try {
                log.info("Hydrating player: {}-{}", member.getName(), member.getRealm());

                String region = "us";
                HydratedPlayer raiderIOHydratedPlayer = hydrationClient.hydrate(region, member.getRealm(),
                        member.getName());

                log.info("Successfully hydrated: {}-{}", raiderIOHydratedPlayer.getName(),
                        raiderIOHydratedPlayer.getRealm());

                double playerScore = scoreCalculatorClient.getPlayerScore(raiderIOHydratedPlayer, targetLevel);

                log.info("Calculated score {} for player: {}-{}", playerScore, raiderIOHydratedPlayer.getName(),
                        raiderIOHydratedPlayer.getRealm());

                HydratedSuccessPlayer enrichedPlayer = new HydratedSuccessPlayer();
                enrichedPlayer.setName(raiderIOHydratedPlayer.getName());
                enrichedPlayer.setRealm(raiderIOHydratedPlayer.getRealm());
                enrichedPlayer.setCharacterClass(raiderIOHydratedPlayer.getCharacterClass());
                enrichedPlayer.setRole(raiderIOHydratedPlayer.getRole());
                enrichedPlayer.setRecentRuns(raiderIOHydratedPlayer.getRecentRuns());
                enrichedPlayer.setCurrentSeasonScore(raiderIOHydratedPlayer.getCurrentSeasonScore());
                enrichedPlayer.setPreviousSeasonScore(raiderIOHydratedPlayer.getPreviousSeasonScore());
                enrichedPlayer.setIsMainRole(raiderIOHydratedPlayer.getIsMainRole());
                enrichedPlayer.setActiveSpecRole(raiderIOHydratedPlayer.getActiveSpecRole());
                enrichedPlayer.setScore(playerScore);

                return enrichedPlayer;

            } catch (Exception e) {
                log.error("Failed to hydrate or score player {}-{}: {}", member.getName(), member.getRealm(),
                        e.getMessage(), e);
                return null; // Or throw new RuntimeException(e); if you prefer to bubble it up
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        log.info("Final hydrated party size: {}", hydratedParty.size());
        return hydratedParty;
    }

    public PartySuccessResponse getPartyConfidence(int targetLevel, String name, String realm) {
        List<HydratedSuccessPlayer> hydratedPLayer = getHydratedParty(targetLevel, name, realm);
        double roleWeight = 1.0;
        double partyScore = 0.0;
        for (HydratedSuccessPlayer player : hydratedPLayer) {
            if (player.getRole().equals("TANK")) {
                roleWeight = 1.25;
            } else if (player.getRole().equals("HEALING")) {
                roleWeight = 1.25;
            } else if (player.getRole().equals("DPS")) {
                roleWeight = 0.81;
            }
            partyScore += player.getScore() * roleWeight;
        }
        double successRate = (partyScore / 50) * 100;
        return new PartySuccessResponse(successRate, hydratedPLayer);
    }
}