package com.workhive.teamservice.domain.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role; // MANAGER or DEVELOPER
}

