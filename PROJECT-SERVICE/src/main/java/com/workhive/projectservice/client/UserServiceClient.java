package com.workhive.projectservice.client;

import com.workhive.projectservice.client.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "USER-SERVICE", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {
    
    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
    
    @GetMapping("/api/users/batch")
    List<UserDto> getUsersByIds(@RequestParam("ids") List<Long> ids);
}
