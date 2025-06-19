package com.sucess.SuccessService.dto;

import java.util.List;

public class PartySuccessResponse {

    private double partySuccessRate;
    private List<HydratedSuccessPlayer> party;

    public PartySuccessResponse(double partySuccessRate, List<HydratedSuccessPlayer> party) {
        this.partySuccessRate = partySuccessRate;
        this.party = party;
    }

    public double getPartySuccessRate() {
        return partySuccessRate;
    }

    public void setPartySuccessRate(double partySuccessRate) {
        this.partySuccessRate = partySuccessRate;
    }

    public List<HydratedSuccessPlayer> getParty() {
        return party;
    }

    public void setParty(List<HydratedSuccessPlayer> party) {
        this.party = party;
    }
}
