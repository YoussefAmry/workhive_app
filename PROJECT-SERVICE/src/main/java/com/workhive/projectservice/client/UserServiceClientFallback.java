package com.workhive.projectservice.client;

import com.workhive.projectservice.client.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserServiceClientFallback implements UserServiceClient {
    
    @Override
    public UserDto getUserById(Long id) {
        log.warn("Fallback: getUserById called for id: {}", id);
        return UserDto.builder()
                .id(id)
                .username("Unknown")
                .firstName("Unknown")
                .lastName("User")
                .email("unknown@workhive.com")
                .role("UNKNOWN")
                .build();
    }
    
    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        log.warn("Fallback: getUsersByIds called for ids: {}", ids);
        List<UserDto> users = new ArrayList<>();
        for (Long id : ids) {
            users.add(getUserById(id));
        }
        return users;
    }
}
