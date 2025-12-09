package com.workhive.userservice.service;

import com.workhive.userservice.domain.dto.LoginRequest;
import com.workhive.userservice.domain.dto.RegisterRequest;
import com.workhive.userservice.domain.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse register(RegisterRequest request);
    String login(LoginRequest request);
    UserResponse getById(Long id);
    UserResponse getByUsername(String username);
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByIds(List<Long> ids);
    void deleteUser(Long id);
}

