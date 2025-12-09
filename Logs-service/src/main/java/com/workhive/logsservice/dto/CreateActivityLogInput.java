package com.workhive.logsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityLogInput {
    private Long userId;
    private String username;
    private String action;
    private String entityType;
    private Long entityId;
    private String details;
    private String ipAddress;
    private String userAgent;
}
