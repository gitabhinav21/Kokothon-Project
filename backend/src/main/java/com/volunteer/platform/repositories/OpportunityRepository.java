package com.volunteer.platform.repositories;

import com.volunteer.platform.models.Opportunity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OpportunityRepository extends MongoRepository<Opportunity, String> {
}
