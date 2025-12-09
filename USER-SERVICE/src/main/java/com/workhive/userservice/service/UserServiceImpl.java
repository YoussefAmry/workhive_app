package com.workhive.userservice.service;

import com.workhive.userservice.domain.dto.LoginRequest;
import com.workhive.userservice.domain.dto.RegisterRequest;
import com.workhive.userservice.domain.dto.UserResponse;
import com.workhive.userservice.domain.entity.Developer;
import com.workhive.userservice.domain.entity.Manager;
import com.workhive.userservice.domain.entity.User;
import com.workhive.userservice.exception.UserNotFoundException;
import com.workhive.userservice.mapper.UserMapper;
import com.workhive.userservice.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    @CircuitBreaker(name = "userService", fallbackMethod = "registerFallback")
    @Retry(name = "userService")
    public UserResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user;
        if (request.getRole().equals("MANAGER"))
            user = new Manager();
        else
            user = new Developer();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        log.info("User login attempt: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        return jwtService.generateToken(user.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "userService", fallbackMethod = "getByIdFallback")
    @Retry(name = "userService")
    public UserResponse getById(Long id) {
        log.info("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "userService", fallbackMethod = "getByUsernameFallback")
    @Retry(name = "userService")
    public UserResponse getByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "userService", fallbackMethod = "getAllUsersFallback")
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "userService", fallbackMethod = "getUsersByIdsFallback")
    public List<UserResponse> getUsersByIds(List<Long> ids) {
        log.info("Fetching users by IDs: {}", ids);
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user: {}", id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // Fallback methods
    private UserResponse registerFallback(RegisterRequest request, Exception e) {
        log.error("Fallback: register failed", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private UserResponse getByIdFallback(Long id, Exception e) {
        log.error("Fallback: getById failed for id: {}", id, e);
        throw new UserNotFoundException("User service unavailable");
    }

    private UserResponse getByUsernameFallback(String username, Exception e) {
        log.error("Fallback: getByUsername failed for username: {}", username, e);
        throw new UserNotFoundException("User service unavailable");
    }

    private List<UserResponse> getAllUsersFallback(Exception e) {
        log.error("Fallback: getAllUsers failed", e);
        return List.of();
    }

    private List<UserResponse> getUsersByIdsFallback(List<Long> ids, Exception e) {
        log.error("Fallback: getUsersByIds failed for ids: {}", ids, e);
        return List.of();
    }
}

