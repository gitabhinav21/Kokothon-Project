package com.volunteer.platform.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "opportunities")
public class Opportunity {

    @Id
    private String id;

    private String title;

    private String description;

    private String organization;

    private List<String> requiredSkills;

    private String location;

    private String date;

    private List<String> applicants; // User IDs
}
