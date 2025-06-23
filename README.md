# Microservice Warm-Up Guide

This project is composed of multiple Java-based Spring Boot microservices hosted on Render. Since Render may spin down inactive services, it’s recommended to “warm up” each service with a `curl` request before depending on it in an orchestrated call.

---

## 🟩 1. PartyTracker Service

Returns the latest party data for a given player.

```bash
curl "https://partytracker.onrender.com/api/party/latest?name=Bedoof&realm=Azralon"
```

---

## 🟦 2. RaiderIOHydration Service

Fetches enriched Raider.IO data for a single character.

```bash
curl "https://raideriohydration.onrender.com/api/hydrate/character?region=us&realm=Drenden&name=Pikaboo"
```

---

## 🟨 3. SuccessScoreCalculationHydration Service

Calculates a success score based on enrichment data.

```bash
curl -X POST "https://successscorecalculationhydration.onrender.com/api/calculate/score?targetLevel=18" ^
  -H "Content-Type: application/json" ^
  -d "{\"recentRuns\":[],\"currentSeasonScore\":2500,\"previousSeasonScore\":2000,\"isMainRole\":true}"
```

---

## 🟥 4. SuccessService

Orchestrates all the services and returns the party's success score.

```bash
curl "https://successservice.onrender.com/api/success?targetLevel=10&name=Bedoof&realm=Azralon"
```

---

## 🔧 Notes

- gotchas that are happening atm that will be fixed https://successservice.onrender.com/api/success?targetLevel=10&name=Franklinn&realm=Emerald%20Dream casing and spacings matter
- These commands are useful to warm up services after inactivity.
- Expect a short delay or a 502/500 response on first call if the service is cold.
- You can store these in a `.bat` or `.sh` script for convenience.
