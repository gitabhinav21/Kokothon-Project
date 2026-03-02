package com.volunteer.platform.services;

import com.volunteer.platform.models.Opportunity;
import com.volunteer.platform.models.User;
import com.volunteer.platform.repositories.OpportunityRepository;
import com.volunteer.platform.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OpportunityRepository opportunityRepository;

    public UserService(UserRepository userRepository, OpportunityRepository opportunityRepository) {
        this.userRepository = userRepository;
        this.opportunityRepository = opportunityRepository;
    }

    public User getUserProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateSkills(String email, List<String> skills) {
        User user = getUserProfile(email);
        user.setSkills(skills);
        return userRepository.save(user);
    }

    public List<Opportunity> getUserApplications(String email) {
        User user = getUserProfile(email);
        return opportunityRepository.findAll().stream()
                .filter(opp -> opp.getApplicants() != null && opp.getApplicants().contains(user.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getLeaderboard() {
        return userRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(b.getImpactScore(), a.getImpactScore()))
                .limit(10)
                .collect(Collectors.toList());
    }
}
