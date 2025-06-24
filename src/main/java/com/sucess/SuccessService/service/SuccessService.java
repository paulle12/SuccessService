package com.sucess.SuccessService.service;

import java.util.List;
import java.util.stream.Collectors;

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

    public SuccessService(
            PartyTrackerClient partyClient,
            RaiderIOHydrationClient hydrationClient,
            SuccessScoreCalculationHydrationClient scoreCalculatorClient) {
        this.partyClient = partyClient;
        this.hydrationClient = hydrationClient;
        this.scoreCalculatorClient = scoreCalculatorClient;
    }

    public List<HydratedSuccessPlayer> getHydratedParty(int targetLevel, String name, String realm) {
        List<PartyMember> baseParty = partyClient.getLatestParty(name, realm);

        List<HydratedSuccessPlayer> hydratedParty = baseParty.stream().map(member -> {
            String region = "us";
            HydratedPlayer raiderIOHydratedPlayer = hydrationClient.hydrate(region, member.getRealm(),
                    member.getName());
            double playerScore = scoreCalculatorClient.getPlayerScore(raiderIOHydratedPlayer, targetLevel);
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
        }).collect(Collectors.toList());

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