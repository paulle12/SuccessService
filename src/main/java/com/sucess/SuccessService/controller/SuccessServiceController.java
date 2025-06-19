package com.sucess.SuccessService.controller;

import org.springframework.web.bind.annotation.*;

import com.sucess.SuccessService.dto.PartySuccessResponse;
import com.sucess.SuccessService.service.SuccessService;

@RestController
@RequestMapping("/api/success")
public class SuccessServiceController {
    private final SuccessService successService;

    public SuccessServiceController(SuccessService successService) {
        this.successService = successService;
    }

    @GetMapping
    public PartySuccessResponse calculate(@RequestParam int targetLevel, @RequestParam String name,
            @RequestParam String realm) {

        return successService.getPartyConfidence(targetLevel, name, realm);
    }
}
