package com.workhive.logsservice.client;

import com.workhive.logsservice.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public UserDto getUserById(Long id) {
        log.warn("Fallback: Unable to fetch user with id: {}", id);
        UserDto fallbackUser = new UserDto();
        fallbackUser.setId(id);
        fallbackUser.setUsername("Unknown User");
        return fallbackUser;
    }
}
