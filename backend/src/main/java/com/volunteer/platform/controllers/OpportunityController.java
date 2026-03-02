package com.volunteer.platform.controllers;

import com.volunteer.platform.models.Opportunity;
import com.volunteer.platform.services.OpportunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/opportunities")
@CrossOrigin(origins = "*")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOpportunities(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(opportunityService.searchOpportunities(title, location, pageable));
    }

    @PostMapping
    public ResponseEntity<Opportunity> createOpportunity(@RequestBody Opportunity opportunity) {
        return ResponseEntity.ok(opportunityService.createOpportunity(opportunity));
    }

    @GetMapping("/match")
    public ResponseEntity<?> getSmartMatches(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        return ResponseEntity.ok(opportunityService.getSmartMatches(email));
    }

    @PostMapping("/apply/{id}")
    public ResponseEntity<?> applyToOpportunity(@PathVariable String id, Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        try {
            String message = opportunityService.applyToOpportunity(id, email);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
