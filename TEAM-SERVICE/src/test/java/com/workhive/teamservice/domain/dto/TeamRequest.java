package com.workhive.teamservice.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class TeamRequest {
    private String name;
    private Long managerId;
    private List<Long> developerIds;
}

