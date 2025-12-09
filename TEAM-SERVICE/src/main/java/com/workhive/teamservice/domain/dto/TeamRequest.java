package com.workhive.teamservice.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequest {
    @NotBlank(message = "Team name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Manager ID is required")
    private Long managerId;
    
    @Builder.Default
    private List<Long> developerIds = new ArrayList<>();
}

