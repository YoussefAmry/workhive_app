package com.workhive.userservice.service;

public interface UserService {
    User register(RegisterRequest request);
    String login(LoginRequest request);
    User getById(Long id);
}

