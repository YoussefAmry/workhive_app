package com.workhive.teamservice.client;

import com.workhive.teamservice.domain.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserClientFallback implements UserClient {
    
    @Override
    public UserDTO getUserById(Long id) {
        log.warn("Fallback: getUserById called for id: {}", id);
        return UserDTO.builder()
                .id(id)
                .username("Unknown")
                .firstName("Unknown")
                .lastName("User")
                .email("unknown@workhive.com")
                .role("UNKNOWN")
                .build();
    }
}
