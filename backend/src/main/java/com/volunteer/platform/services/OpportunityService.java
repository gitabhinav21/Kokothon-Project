package com.volunteer.platform.services;

import com.volunteer.platform.models.Opportunity;
import com.volunteer.platform.models.User;
import com.volunteer.platform.repositories.OpportunityRepository;
import com.volunteer.platform.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public OpportunityService(OpportunityRepository opportunityRepository, UserRepository userRepository,
            MongoTemplate mongoTemplate) {
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Opportunity> getAllOpportunities() {
        return opportunityRepository.findAll();
    }

    public Page<Opportunity> searchOpportunities(String title, String location, Pageable pageable) {
        Query query = new Query().with(pageable);
        if (title != null && !title.isEmpty()) {
            query.addCriteria(Criteria.where("title").regex(title, "i"));
        }
        if (location != null && !location.isEmpty()) {
            query.addCriteria(Criteria.where("location").regex(location, "i"));
        }

        List<Opportunity> list = mongoTemplate.find(query, Opportunity.class);
        long count = mongoTemplate.count(query.skip(-1).limit(-1), Opportunity.class);

        return org.springframework.data.support.PageableExecutionUtils.getPage(list, pageable, () -> count);
    }

    public Opportunity createOpportunity(Opportunity opportunity) {
        opportunity.setApplicants(new ArrayList<>());
        return opportunityRepository.save(opportunity);
    }

    public Opportunity getOpportunityById(String id) {
        return opportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opportunity not found"));
    }

    public String applyToOpportunity(String opportunityId, String userEmail) {
        Opportunity opportunity = getOpportunityById(opportunityId);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (opportunity.getApplicants().contains(user.getId())) {
            return "Already applied to this opportunity";
        }

        opportunity.getApplicants().add(user.getId());
        opportunityRepository.save(opportunity);
        return "Applied successfully";
    }

    public List<Map<String, Object>> getSmartMatches(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> userSkills = user.getSkills() != null ? user.getSkills() : new ArrayList<>();
        List<Opportunity> allOpportunities = opportunityRepository.findAll();

        return allOpportunities.stream()
                .map(opp -> {
                    double matchPercentage = calculateMatchPercentage(userSkills, opp.getRequiredSkills());
                    Map<String, Object> result = new HashMap<>();
                    result.put("opportunity", opp);
                    result.put("matchPercentage", matchPercentage);
                    return result;
                })
                .sorted((a, b) -> Double.compare((double) b.get("matchPercentage"), (double) a.get("matchPercentage")))
                .collect(Collectors.toList());
    }

    private double calculateMatchPercentage(List<String> userSkills, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty())
            return 100.0;
        if (userSkills == null || userSkills.isEmpty())
            return 0.0;

        long matchedCount = requiredSkills.stream()
                .filter(skill -> userSkills.stream().anyMatch(s -> s.equalsIgnoreCase(skill)))
                .count();

        return ((double) matchedCount / requiredSkills.size()) * 100.0;
    }
}
