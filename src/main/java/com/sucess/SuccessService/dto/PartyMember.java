package com.sucess.SuccessService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PartyMember {

    private Long id;
    private String name;
    private String realm;

    // JSON lists this as "class", so we map it to a valid Java field name
    @JsonProperty("class")
    private String className;

    private String role;

    @JsonProperty("party_id")
    private String partyId;

    // === Getters ===

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRealm() {
        return realm;
    }

    public String getClassName() {
        return className;
    }

    public String getRole() {
        return role;
    }

    public String getPartyId() {
        return partyId;
    }

    // === Setters ===

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }
}